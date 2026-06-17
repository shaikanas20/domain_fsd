package com.cinereserve.catalog.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "screens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Screen {
    @Id
    private String id;
    
    private String theatreId;
    private String name;
    private String seatLayoutId;
}
