package com.cinereserve.catalog.controller;

import com.cinereserve.catalog.dto.ApiResponse;
import com.cinereserve.catalog.dto.MovieRequest;
import com.cinereserve.catalog.model.Movie;
import com.cinereserve.catalog.security.UserContext;
import com.cinereserve.catalog.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    private void checkAdminOrOwner() {
        String role = UserContext.getRole();
        if (!"ADMIN".equals(role) && !"THEATRE_OWNER".equals(role)) {
            throw new RuntimeException("Access denied. Admin or Theatre Owner role required.");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Movie>> createMovie(@Valid @RequestBody MovieRequest request) {
        checkAdminOrOwner();
        Movie movie = movieService.createMovie(request);
        return ResponseEntity.ok(ApiResponse.success(movie, "Movie created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Movie>> updateMovie(@PathVariable String id, @Valid @RequestBody MovieRequest request) {
        checkAdminOrOwner();
        Movie movie = movieService.updateMovie(id, request);
        return ResponseEntity.ok(ApiResponse.success(movie, "Movie updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable String id) {
        checkAdminOrOwner();
        movieService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Movie deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Movie>> getMovieById(@PathVariable String id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(ApiResponse.success(movie, "Movie retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Movie>>> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "releaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Movie> movies = movieService.searchMovies(title, genre, language, minRating, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(movies, "Movies retrieved successfully"));
    }
}
