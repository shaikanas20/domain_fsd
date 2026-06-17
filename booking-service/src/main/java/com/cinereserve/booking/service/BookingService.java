package com.cinereserve.booking.service;

import com.cinereserve.booking.dto.BookingRequest;
import com.cinereserve.booking.dto.BookingResponse;
import com.cinereserve.booking.dto.ShowSeatResponse;
import com.cinereserve.booking.event.BookingEvent;
import com.cinereserve.booking.event.BookingEventProducer;
import com.cinereserve.booking.exception.BookingException;
import com.cinereserve.booking.model.*;
import com.cinereserve.booking.repository.BookingRepository;
import com.cinereserve.booking.repository.ShowRepository;
import com.cinereserve.booking.repository.ShowSeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final BookingEventProducer eventProducer;
    private final org.springframework.cache.CacheManager cacheManager;

    public BookingService(BookingRepository bookingRepository,
                          ShowRepository showRepository,
                          ShowSeatRepository showSeatRepository,
                          BookingEventProducer eventProducer,
                          org.springframework.cache.CacheManager cacheManager) {
        this.bookingRepository = bookingRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.eventProducer = eventProducer;
        this.cacheManager = cacheManager;
    }

    private void evictSeatAvailabilityCache(Long showId) {
        org.springframework.cache.Cache cache = cacheManager.getCache("seat_availability");
        if (cache != null) {
            cache.evict(showId);
        }
    }

    @Transactional
    public BookingResponse initiateBooking(Long userId, BookingRequest request) {
        Show show = showRepository.findById(request.showId())
                .orElseThrow(() -> new BookingException("Show not found with ID: " + request.showId()));

        // Pessimistic write locking on the selected seats to prevent race conditions (double bookings)
        List<ShowSeat> selectedSeats = showSeatRepository.findByShowIdAndSeatNumberInWithLock(
                request.showId(), request.seatNumbers()
        );

        if (selectedSeats.size() != request.seatNumbers().size()) {
            throw new BookingException("Some selected seats do not exist in the screen configuration.");
        }

        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);

        for (ShowSeat seat : selectedSeats) {
            if (seat.getStatus() == SeatStatus.BOOKED) {
                throw new BookingException("Seat " + seat.getSeatNumber() + " is already booked.");
            }
            if (seat.getStatus() == SeatStatus.LOCKED) {
                // Check if the lock is still valid (locked in the last 5 minutes)
                if (seat.getLockTime() != null && seat.getLockTime().isAfter(cutoffTime)) {
                    throw new BookingException("Seat " + seat.getSeatNumber() + " is currently locked by another customer.");
                }
            }
        }

        // Lock the seats for the current user
        double totalAmount = 0.0;
        for (ShowSeat seat : selectedSeats) {
            seat.setStatus(SeatStatus.LOCKED);
            seat.setLockedBy(userId);
            seat.setLockTime(LocalDateTime.now());
            
            Double price = show.getPriceMap().get(seat.getSeatType());
            if (price == null) {
                price = 100.0; // Default price if not matching
            }
            totalAmount += price;
        }

        showSeatRepository.saveAll(selectedSeats);

        Booking booking = Booking.builder()
                .userId(userId)
                .show(show)
                .totalAmount(totalAmount)
                .status(BookingStatus.INITIATED)
                .seatNumbers(request.seatNumbers())
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        evictSeatAvailabilityCache(request.showId());

        return mapToBookingResponse(savedBooking);
    }

    @Transactional
    public BookingResponse confirmBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking record not found."));

        if (!booking.getUserId().equals(userId)) {
            throw new BookingException("Unauthorized transaction. Booking belongs to another user.");
        }

        if (booking.getStatus() != BookingStatus.INITIATED) {
            throw new BookingException("Booking cannot be confirmed. Current status: " + booking.getStatus());
        }

        // Check if the lock time has expired (5-minute expiration rule)
        if (booking.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
            // Cancel booking and release seats
            cancelInitiatedBooking(booking);
            throw new BookingException("Reservation expired. Seats were locked for more than 5 minutes.");
        }

        // Lock seats as booked
        List<ShowSeat> seats = showSeatRepository.findByShowIdAndSeatNumberInWithLock(
                booking.getShow().getId(), booking.getSeatNumbers()
        );

        for (ShowSeat seat : seats) {
            seat.setStatus(SeatStatus.BOOKED);
            seat.setLockedBy(null);
            seat.setLockTime(null);
        }

        showSeatRepository.saveAll(seats);

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking confirmedBooking = bookingRepository.save(booking);

        evictSeatAvailabilityCache(booking.getShow().getId());

        // Publish event to RabbitMQ
        eventProducer.sendBookingConfirmed(new BookingEvent(
                confirmedBooking.getId(),
                confirmedBooking.getUserId(),
                confirmedBooking.getShow().getId(),
                confirmedBooking.getTotalAmount(),
                confirmedBooking.getSeatNumbers(),
                confirmedBooking.getStatus().name(),
                LocalDateTime.now()
        ));

        return mapToBookingResponse(confirmedBooking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found."));

        // Admins can cancel any booking, users can cancel their own
        if (!booking.getUserId().equals(userId)) {
            throw new BookingException("Unauthorized to cancel this booking.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled.");
        }

        // Release seats
        List<ShowSeat> seats = showSeatRepository.findByShowIdAndSeatNumberInWithLock(
                booking.getShow().getId(), booking.getSeatNumbers()
        );

        for (ShowSeat seat : seats) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockedBy(null);
            seat.setLockTime(null);
        }

        showSeatRepository.saveAll(seats);

        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);

        evictSeatAvailabilityCache(booking.getShow().getId());

        // Publish event to RabbitMQ
        eventProducer.sendBookingCancelled(new BookingEvent(
                cancelledBooking.getId(),
                cancelledBooking.getUserId(),
                cancelledBooking.getShow().getId(),
                cancelledBooking.getTotalAmount(),
                cancelledBooking.getSeatNumbers(),
                cancelledBooking.getStatus().name(),
                LocalDateTime.now()
        ));

        return mapToBookingResponse(cancelledBooking);
    }

    @org.springframework.cache.annotation.Cacheable(value = "seat_availability", key = "#showId")
    @Transactional
    public List<ShowSeatResponse> getSeatAvailability(Long showId) {
        List<ShowSeat> seats = showSeatRepository.findByShowId(showId);
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(5);
        boolean dirty = false;

        // Dynamic self-healing mechanism to clean up expired locked seats
        for (ShowSeat seat : seats) {
            if (seat.getStatus() == SeatStatus.LOCKED && seat.getLockTime() != null && seat.getLockTime().isBefore(cutoff)) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setLockedBy(null);
                seat.setLockTime(null);
                dirty = true;
            }
        }

        if (dirty) {
            showSeatRepository.saveAll(seats);
        }

        return seats.stream()
                .map(s -> new ShowSeatResponse(s.getId(), s.getSeatNumber(), s.getSeatType(), s.getStatus(), s.getLockedBy()))
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingHistory(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    private void cancelInitiatedBooking(Booking booking) {
        List<ShowSeat> seats = showSeatRepository.findByShowIdAndSeatNumberIn(
                booking.getShow().getId(), booking.getSeatNumbers()
        );
        for (ShowSeat seat : seats) {
            if (seat.getLockedBy() != null && seat.getLockedBy().equals(booking.getUserId())) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setLockedBy(null);
                seat.setLockTime(null);
            }
        }
        showSeatRepository.saveAll(seats);
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        evictSeatAvailabilityCache(booking.getShow().getId());
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getShow().getId(),
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getSeatNumbers(),
                booking.getCreatedAt()
        );
    }
}
