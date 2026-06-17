package com.cinereserve.booking.dto;

import com.cinereserve.booking.model.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
    Long id,
    Long userId,
    Long showId,
    Double totalAmount,
    BookingStatus status,
    List<String> seatNumbers,
    LocalDateTime createdAt
) {}
