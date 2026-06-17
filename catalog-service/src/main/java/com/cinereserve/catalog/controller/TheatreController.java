package com.cinereserve.catalog.controller;

import com.cinereserve.catalog.dto.ApiResponse;
import com.cinereserve.catalog.dto.TheatreRequest;
import com.cinereserve.catalog.model.Theatre;
import com.cinereserve.catalog.security.UserContext;
import com.cinereserve.catalog.service.TheatreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/theatres")
public class TheatreController {

    private final TheatreService theatreService;

    public TheatreController(TheatreService theatreService) {
        this.theatreService = theatreService;
    }

    private void checkAdmin() {
        String role = UserContext.getRole();
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Access denied. Admin role required.");
        }
    }

    private void checkAdminOrOwner() {
        String role = UserContext.getRole();
        if (!"ADMIN".equals(role) && !"THEATRE_OWNER".equals(role)) {
            throw new RuntimeException("Access denied. Admin or Theatre Owner role required.");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Theatre>> createTheatre(@Valid @RequestBody TheatreRequest request) {
        checkAdminOrOwner();
        Theatre theatre = theatreService.createTheatre(request);
        return ResponseEntity.ok(ApiResponse.success(theatre, "Theatre registration request submitted successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Theatre>> updateTheatre(@PathVariable String id, @Valid @RequestBody TheatreRequest request) {
        checkAdminOrOwner();
        Theatre theatre = theatreService.updateTheatre(id, request);
        return ResponseEntity.ok(ApiResponse.success(theatre, "Theatre details updated successfully"));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Theatre>> approveTheatre(
            @PathVariable String id, 
            @RequestParam String status
    ) {
        checkAdmin();
        Theatre theatre = theatreService.approveTheatre(id, status);
        return ResponseEntity.ok(ApiResponse.success(theatre, "Theatre approval status updated to " + status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTheatre(@PathVariable String id) {
        checkAdmin();
        theatreService.deleteTheatre(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Theatre deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Theatre>> getTheatreById(@PathVariable String id) {
        Theatre theatre = theatreService.getTheatreById(id);
        return ResponseEntity.ok(ApiResponse.success(theatre, "Theatre retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Theatre>>> getAllTheatres(@RequestParam(required = false) String status) {
        List<Theatre> theatres = theatreService.getAllTheatres(status);
        return ResponseEntity.ok(ApiResponse.success(theatres, "Theatres retrieved successfully"));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<ApiResponse<List<Theatre>>> getTheatresByLocation(
            @PathVariable String locationId,
            @RequestParam(required = false) String status
    ) {
        List<Theatre> theatres = theatreService.getTheatresByLocation(locationId, status);
        return ResponseEntity.ok(ApiResponse.success(theatres, "Theatres retrieved successfully for location"));
    }
}
