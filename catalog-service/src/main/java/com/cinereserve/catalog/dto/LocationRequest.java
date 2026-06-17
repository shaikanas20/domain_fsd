package com.cinereserve.catalog.dto;

import jakarta.validation.constraints.NotBlank;

public record LocationRequest(
    @NotBlank(message = "Location name is required")
    String name,

    @NotBlank(message = "City is required")
    String city,

    @NotBlank(message = "State is required")
    String state
) {}
