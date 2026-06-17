package com.cinereserve.booking;

import com.cinereserve.booking.dto.BookingRequest;
import com.cinereserve.booking.dto.BookingResponse;
import com.cinereserve.booking.event.BookingEventProducer;
import com.cinereserve.booking.exception.BookingException;
import com.cinereserve.booking.model.*;
import com.cinereserve.booking.repository.BookingRepository;
import com.cinereserve.booking.repository.ShowRepository;
import com.cinereserve.booking.repository.ShowSeatRepository;
import com.cinereserve.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private ShowSeatRepository showSeatRepository;

    @Mock
    private BookingEventProducer eventProducer;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitiateBooking_Success() {
        Long userId = 1L;
        Map<String, Double> prices = new HashMap<>();
        prices.put("NORMAL", 150.0);

        Show show = Show.builder()
                .id(10L)
                .movieId("movie1")
                .screenId("screen1")
                .theatreId("theatre1")
                .showDate(LocalDate.now())
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(20, 0))
                .priceMap(prices)
                .build();

        ShowSeat seat = ShowSeat.builder()
                .id(100L)
                .show(show)
                .seatNumber("A1")
                .seatType("NORMAL")
                .status(SeatStatus.AVAILABLE)
                .build();

        BookingRequest request = new BookingRequest(10L, Collections.singletonList("A1"));

        when(showRepository.findById(10L)).thenReturn(Optional.of(show));
        when(showSeatRepository.findByShowIdAndSeatNumberInWithLock(10L, Collections.singletonList("A1")))
                .thenReturn(Collections.singletonList(seat));
        
        Booking mockBooking = Booking.builder()
                .id(1000L)
                .userId(userId)
                .show(show)
                .totalAmount(150.0)
                .status(BookingStatus.INITIATED)
                .seatNumbers(Collections.singletonList("A1"))
                .createdAt(LocalDateTime.now())
                .build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        BookingResponse response = bookingService.initiateBooking(userId, request);

        assertNotNull(response);
        assertEquals(BookingStatus.INITIATED, response.status());
        assertEquals(150.0, response.totalAmount());
        assertEquals("A1", response.seatNumbers().get(0));
        verify(showSeatRepository, times(1)).saveAll(anyList());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testInitiateBooking_SeatAlreadyBooked() {
        Long userId = 1L;
        Show show = Show.builder().id(10L).build();
        ShowSeat seat = ShowSeat.builder()
                .seatNumber("A1")
                .status(SeatStatus.BOOKED)
                .build();

        BookingRequest request = new BookingRequest(10L, Collections.singletonList("A1"));

        when(showRepository.findById(10L)).thenReturn(Optional.of(show));
        when(showSeatRepository.findByShowIdAndSeatNumberInWithLock(10L, Collections.singletonList("A1")))
                .thenReturn(Collections.singletonList(seat));

        assertThrows(BookingException.class, () -> bookingService.initiateBooking(userId, request));
    }
}
