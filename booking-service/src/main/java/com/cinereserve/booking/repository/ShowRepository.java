package com.cinereserve.booking.repository;

import com.cinereserve.booking.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByMovieId(String movieId);
    List<Show> findByTheatreIdAndShowDate(String theatreId, LocalDate showDate);
}
