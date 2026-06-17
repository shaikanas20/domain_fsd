package com.cinereserve.catalog.dto;

import jakarta.validation.constraints.NotBlank;

public record TheatreRequest(
    @NotBlank(message = "Theatre name is required")
    String name,

    @NotBlank(message = "Location ID is required")
    String locationId,

    @NotBlank(message = "Address is required")
    String address
) {}
