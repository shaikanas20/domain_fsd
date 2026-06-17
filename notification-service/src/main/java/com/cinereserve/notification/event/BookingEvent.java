package com.cinereserve.notification.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record BookingEvent(
    Long bookingId,
    Long userId,
    Long showId,
    Double totalAmount,
    List<String> seatNumbers,
    String status,
    LocalDateTime timestamp
) implements Serializable {}
