package com.cinereserve.catalog.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "seat_layouts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatLayout {
    @Id
    private String id;
    
    private String name;
    private List<String> rowNames; // e.g. ["A", "B", "C", "D"]
    private Integer colCount;
    private List<SeatConfig> seats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatConfig {
        private String rowName;
        private Integer colIndex;
        private String seatType; // e.g. VIP, PREMIUM, NORMAL, EMPTY (for gaps)
    }
}
