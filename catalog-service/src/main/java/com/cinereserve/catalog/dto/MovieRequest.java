package com.cinereserve.catalog.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record MovieRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Description is required")
    String description,

    @NotEmpty(message = "At least one genre is required")
    List<String> genres,

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be positive")
    Integer duration,

    @Min(value = 0, message = "Rating cannot be negative")
    @Max(value = 10, message = "Rating cannot exceed 10")
    Double rating,

    @NotEmpty(message = "At least one language is required")
    List<String> languages,

    @NotNull(message = "Release date is required")
    LocalDate releaseDate,

    @NotBlank(message = "Poster URL is required")
    String poster,

    String trailer,

    @NotBlank(message = "Status is required")
    String status
) {}
