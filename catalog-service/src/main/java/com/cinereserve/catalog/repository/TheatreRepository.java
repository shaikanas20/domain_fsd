package com.cinereserve.catalog.repository;

import com.cinereserve.catalog.model.Theatre;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheatreRepository extends MongoRepository<Theatre, String> {
    List<Theatre> findByLocationId(String locationId);
    List<Theatre> findByLocationIdAndStatus(String locationId, String status);
    List<Theatre> findByStatus(String status);
}
