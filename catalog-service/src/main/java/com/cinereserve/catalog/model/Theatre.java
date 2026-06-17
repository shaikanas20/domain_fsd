package com.cinereserve.catalog.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "theatres")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Theatre {
    @Id
    private String id;
    
    private String name;
    private String locationId;
    private String address;
    private String status; // PENDING, APPROVED, REJECTED
}
