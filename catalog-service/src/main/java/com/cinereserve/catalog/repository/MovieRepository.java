package com.cinereserve.catalog.repository;

import com.cinereserve.catalog.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {
    Page<Movie> findByStatus(String status, Pageable pageable);
    List<Movie> findByStatus(String status);
}
