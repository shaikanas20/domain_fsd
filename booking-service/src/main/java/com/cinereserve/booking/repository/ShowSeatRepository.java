package com.cinereserve.booking.repository;

import com.cinereserve.booking.model.ShowSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {
    
    List<ShowSeat> findByShowId(Long showId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ShowSeat s WHERE s.show.id = :showId AND s.seatNumber IN :seatNumbers")
    List<ShowSeat> findByShowIdAndSeatNumberInWithLock(Long showId, List<String> seatNumbers);

    List<ShowSeat> findByShowIdAndSeatNumberIn(Long showId, List<String> seatNumbers);
}
