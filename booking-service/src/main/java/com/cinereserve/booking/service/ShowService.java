package com.cinereserve.booking.service;

import com.cinereserve.booking.dto.ShowRequest;
import com.cinereserve.booking.dto.ShowResponse;
import com.cinereserve.booking.exception.BookingException;
import com.cinereserve.booking.model.SeatStatus;
import com.cinereserve.booking.model.Show;
import com.cinereserve.booking.model.ShowSeat;
import com.cinereserve.booking.repository.ShowRepository;
import com.cinereserve.booking.repository.ShowSeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;

    public ShowService(ShowRepository showRepository, ShowSeatRepository showSeatRepository) {
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
    }

    @org.springframework.cache.annotation.CacheEvict(value = "shows", allEntries = true)
    @Transactional
    public ShowResponse createShow(ShowRequest request) {
        Show show = Show.builder()
                .movieId(request.movieId())
                .screenId(request.screenId())
                .theatreId(request.theatreId())
                .showDate(request.showDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .priceMap(request.priceMap())
                .build();

        Show savedShow = showRepository.save(show);

        List<ShowSeat> showSeats = request.seats().stream()
                .map(seatDto -> ShowSeat.builder()
                        .show(savedShow)
                        .seatNumber(seatDto.seatNumber())
                        .seatType(seatDto.seatType())
                        .status(SeatStatus.AVAILABLE)
                        .build())
                .collect(Collectors.toList());

        showSeatRepository.saveAll(showSeats);

        return mapToShowResponse(savedShow);
    }

    @org.springframework.cache.annotation.Cacheable(value = "shows", key = "#id")
    public ShowResponse getShowById(Long id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new BookingException("Show not found with ID: " + id));
        return mapToShowResponse(show);
    }

    public List<ShowResponse> getShowsByTheatreAndDate(String theatreId, LocalDate date) {
        return showRepository.findByTheatreIdAndShowDate(theatreId, date).stream()
                .map(this::mapToShowResponse)
                .collect(Collectors.toList());
    }

    public List<ShowResponse> getShowsByMovie(String movieId) {
        return showRepository.findByMovieId(movieId).stream()
                .map(this::mapToShowResponse)
                .collect(Collectors.toList());
    }

    private ShowResponse mapToShowResponse(Show show) {
        return new ShowResponse(
                show.getId(),
                show.getMovieId(),
                show.getScreenId(),
                show.getTheatreId(),
                show.getShowDate(),
                show.getStartTime(),
                show.getEndTime(),
                show.getPriceMap()
        );
    }
}
