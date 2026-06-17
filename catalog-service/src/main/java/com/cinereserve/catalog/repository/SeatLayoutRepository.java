package com.cinereserve.catalog.repository;

import com.cinereserve.catalog.model.SeatLayout;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatLayoutRepository extends MongoRepository<SeatLayout, String> {
}
