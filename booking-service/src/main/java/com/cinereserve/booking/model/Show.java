package com.cinereserve.booking.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Entity
@Table(name = "shows")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movie_id", nullable = false)
    private String movieId;

    @Column(name = "screen_id", nullable = false)
    private String screenId;

    @Column(name = "theatre_id", nullable = false)
    private String theatreId;

    @Column(name = "show_date", nullable = false)
    private LocalDate showDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "show_prices", joinColumns = @JoinColumn(name = "show_id"))
    @MapKeyColumn(name = "seat_type")
    @Column(name = "price")
    private Map<String, Double> priceMap;
}
