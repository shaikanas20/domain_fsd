package com.cinereserve.catalog.dto;

import jakarta.validation.constraints.NotBlank;

public record ScreenRequest(
    @NotBlank(message = "Theatre ID is required")
    String theatreId,

    @NotBlank(message = "Screen name is required")
    String name,

    @NotBlank(message = "Seat Layout ID is required")
    String seatLayoutId
) {}
