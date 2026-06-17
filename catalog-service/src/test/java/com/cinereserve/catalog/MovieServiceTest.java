package com.cinereserve.catalog;

import com.cinereserve.catalog.dto.MovieRequest;
import com.cinereserve.catalog.exception.CatalogException;
import com.cinereserve.catalog.model.Movie;
import com.cinereserve.catalog.repository.MovieRepository;
import com.cinereserve.catalog.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMovie_Success() {
        MovieRequest request = new MovieRequest(
                "Inception", "A thief steals corporate secrets", 
                Collections.singletonList("Sci-Fi"), 148, 8.8, 
                Collections.singletonList("English"), LocalDate.of(2010, 7, 16), 
                "poster_url", "trailer_url", "NOW_SHOWING"
        );

        Movie movie = Movie.builder()
                .id("movie1")
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

        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        Movie createdMovie = movieService.createMovie(request);

        assertNotNull(createdMovie);
        assertEquals("Inception", createdMovie.getTitle());
        assertEquals("movie1", createdMovie.getId());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void testGetMovieById_Success() {
        Movie movie = Movie.builder()
                .id("movie1")
                .title("Inception")
                .build();

        when(movieRepository.findById("movie1")).thenReturn(Optional.of(movie));

        Movie result = movieService.getMovieById("movie1");

        assertNotNull(result);
        assertEquals("Inception", result.getTitle());
    }

    @Test
    void testGetMovieById_NotFound() {
        when(movieRepository.findById("movieX")).thenReturn(Optional.empty());

        assertThrows(CatalogException.class, () -> movieService.getMovieById("movieX"));
    }
}
