package com.cinereserve.catalog.service;

import com.cinereserve.catalog.dto.MovieRequest;
import com.cinereserve.catalog.exception.CatalogException;
import com.cinereserve.catalog.model.Movie;
import com.cinereserve.catalog.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MongoTemplate mongoTemplate;

    public MovieService(MovieRepository movieRepository, MongoTemplate mongoTemplate) {
        this.movieRepository = movieRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @org.springframework.cache.annotation.CacheEvict(value = "movies", allEntries = true)
    public Movie createMovie(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.title())
                .description(request.description())
                .genres(request.genres())
                .duration(request.duration())
                .rating(request.rating())
                .languages(request.languages())
                .releaseDate(request.releaseDate())
                .poster(request.poster())
                .trailer(request.trailer())
                .status(request.status())
                .build();
        return movieRepository.save(movie);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "movies", key = "#id")
    public Movie updateMovie(String id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Movie not found with ID: " + id));

        movie.setTitle(request.title());
        movie.setDescription(request.description());
        movie.setGenres(request.genres());
        movie.setDuration(request.duration());
        movie.setRating(request.rating());
        movie.setLanguages(request.languages());
        movie.setReleaseDate(request.releaseDate());
        movie.setPoster(request.poster());
        movie.setTrailer(request.trailer());
        movie.setStatus(request.status());

        return movieRepository.save(movie);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "movies", key = "#id")
    public void deleteMovie(String id) {
        if (!movieRepository.existsById(id)) {
            throw new CatalogException("Movie not found with ID: " + id);
        }
        movieRepository.deleteById(id);
    }

    @org.springframework.cache.annotation.Cacheable(value = "movies", key = "#id")
    public Movie getMovieById(String id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new CatalogException("Movie not found with ID: " + id));
    }

    public Page<Movie> searchMovies(String title, String genre, String language, Double minRating, String status, Pageable pageable) {
        // Build a criteria-only query (no pagination) for the total count
        Query countQuery = new Query();
        // Build the data query with pagination applied
        Query dataQuery = new Query().with(pageable);

        if (title != null && !title.isEmpty()) {
            Criteria c = Criteria.where("title").regex(title, "i");
            countQuery.addCriteria(c);
            dataQuery.addCriteria(c);
        }
        if (genre != null && !genre.isEmpty()) {
            Criteria c = Criteria.where("genres").regex(genre, "i");
            countQuery.addCriteria(c);
            dataQuery.addCriteria(c);
        }
        if (language != null && !language.isEmpty()) {
            Criteria c = Criteria.where("languages").regex(language, "i");
            countQuery.addCriteria(c);
            dataQuery.addCriteria(c);
        }
        if (minRating != null) {
            Criteria c = Criteria.where("rating").gte(minRating);
            countQuery.addCriteria(c);
            dataQuery.addCriteria(c);
        }
        if (status != null && !status.isEmpty()) {
            Criteria c = Criteria.where("status").is(status);
            countQuery.addCriteria(c);
            dataQuery.addCriteria(c);
        }

        List<Movie> list = mongoTemplate.find(dataQuery, Movie.class);
        return PageableExecutionUtils.getPage(list, pageable, () -> mongoTemplate.count(countQuery, Movie.class));
    }
}
