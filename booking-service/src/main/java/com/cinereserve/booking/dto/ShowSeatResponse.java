package com.cinereserve.booking.dto;

import com.cinereserve.booking.model.SeatStatus;

public record ShowSeatResponse(
    Long id,
    String seatNumber,
    String seatType,
    SeatStatus status,
    Long lockedBy
) {}
