package com.cinereserve.catalog.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "movies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    private String id;
    
    private String title;
    private String description;
    private List<String> genres;
    private Integer duration; // in minutes
    private Double rating;
    private List<String> languages;
    private LocalDate releaseDate;
    private String poster;
    private String trailer;
    private String status; // NOW_SHOWING, UPCOMING, INACTIVE
}
