package com.cinereserve.booking.controller;

import com.cinereserve.booking.dto.ApiResponse;
import com.cinereserve.booking.dto.ShowRequest;
import com.cinereserve.booking.dto.ShowResponse;
import com.cinereserve.booking.security.UserContext;
import com.cinereserve.booking.service.ShowService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/booking/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    private void checkAdminOrOwner() {
        String role = UserContext.getRole();
        if (!"ADMIN".equals(role) && !"THEATRE_OWNER".equals(role)) {
            throw new RuntimeException("Access denied. Admin or Theatre Owner role required.");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShowResponse>> createShow(@Valid @RequestBody ShowRequest request) {
        checkAdminOrOwner();
        ShowResponse response = showService.createShow(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Show created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowResponse>> getShowById(@PathVariable Long id) {
        ShowResponse response = showService.getShowById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Show retrieved successfully"));
    }

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getShowsByTheatre(
            @PathVariable String theatreId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<ShowResponse> responses = showService.getShowsByTheatreAndDate(theatreId, date);
        return ResponseEntity.ok(ApiResponse.success(responses, "Shows retrieved successfully"));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getShowsByMovie(@PathVariable String movieId) {
        List<ShowResponse> responses = showService.getShowsByMovie(movieId);
        return ResponseEntity.ok(ApiResponse.success(responses, "Shows retrieved successfully"));
    }
}
