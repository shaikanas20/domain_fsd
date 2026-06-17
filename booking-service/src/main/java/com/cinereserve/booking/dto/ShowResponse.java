package com.cinereserve.booking.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public record ShowResponse(
    Long id,
    String movieId,
    String screenId,
    String theatreId,
    LocalDate showDate,
    LocalTime startTime,
    LocalTime endTime,
    Map<String, Double> priceMap
) {}
