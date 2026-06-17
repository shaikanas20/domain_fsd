package com.cinereserve.catalog.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record SeatLayoutRequest(
    @NotBlank(message = "Layout name is required")
    String name,

    @NotEmpty(message = "Row names are required")
    List<String> rowNames,

    @NotNull(message = "Column count is required")
    @Min(value = 1, message = "Column count must be at least 1")
    Integer colCount,

    @NotEmpty(message = "Seats configurations are required")
    List<SeatConfigDTO> seats
) {
    public record SeatConfigDTO(
        @NotBlank(message = "Row name is required")
        String rowName,

        @NotNull(message = "Column index is required")
        @Min(value = 1, message = "Column index must be positive")
        Integer colIndex,

        @NotBlank(message = "Seat type is required")
        String seatType
    ) {}
}
