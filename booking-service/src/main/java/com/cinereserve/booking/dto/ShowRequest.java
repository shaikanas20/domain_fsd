package com.cinereserve.booking.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public record ShowRequest(
    @NotBlank(message = "Movie ID is required")
    String movieId,

    @NotBlank(message = "Screen ID is required")
    String screenId,

    @NotBlank(message = "Theatre ID is required")
    String theatreId,

    @NotNull(message = "Show date is required")
    LocalDate showDate,

    @NotNull(message = "Start time is required")
    LocalTime startTime,

    @NotNull(message = "End time is required")
    LocalTime endTime,

    @NotEmpty(message = "Price map is required")
    Map<String, Double> priceMap,

    @NotEmpty(message = "Seats layout to initialize is required")
    List<SeatInitDTO> seats
) {
    public record SeatInitDTO(
        @NotBlank(message = "Seat number is required")
        String seatNumber,

        @NotBlank(message = "Seat type is required")
        String seatType
    ) {}
}
