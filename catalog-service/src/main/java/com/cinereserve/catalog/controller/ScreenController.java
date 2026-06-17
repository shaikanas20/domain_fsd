package com.cinereserve.catalog.controller;

import com.cinereserve.catalog.dto.ApiResponse;
import com.cinereserve.catalog.dto.ScreenRequest;
import com.cinereserve.catalog.model.Screen;
import com.cinereserve.catalog.security.UserContext;
import com.cinereserve.catalog.service.ScreenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/screens")
public class ScreenController {

    private final ScreenService screenService;

    public ScreenController(ScreenService screenService) {
        this.screenService = screenService;
    }

    private void checkAdminOrOwner() {
        String role = UserContext.getRole();
        if (!"ADMIN".equals(role) && !"THEATRE_OWNER".equals(role)) {
            throw new RuntimeException("Access denied. Admin or Theatre Owner role required.");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Screen>> createScreen(@Valid @RequestBody ScreenRequest request) {
        checkAdminOrOwner();
        Screen screen = screenService.createScreen(request);
        return ResponseEntity.ok(ApiResponse.success(screen, "Screen created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Screen>> updateScreen(@PathVariable String id, @Valid @RequestBody ScreenRequest request) {
        checkAdminOrOwner();
        Screen screen = screenService.updateScreen(id, request);
        return ResponseEntity.ok(ApiResponse.success(screen, "Screen updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteScreen(@PathVariable String id) {
        checkAdminOrOwner();
        screenService.deleteScreen(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Screen deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Screen>> getScreenById(@PathVariable String id) {
        Screen screen = screenService.getScreenById(id);
        return ResponseEntity.ok(ApiResponse.success(screen, "Screen retrieved successfully"));
    }

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<ApiResponse<List<Screen>>> getScreensByTheatre(@PathVariable String theatreId) {
        List<Screen> screens = screenService.getScreensByTheatre(theatreId);
        return ResponseEntity.ok(ApiResponse.success(screens, "Screens retrieved successfully for theatre"));
    }
}
