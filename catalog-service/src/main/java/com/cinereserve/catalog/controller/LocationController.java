package com.cinereserve.catalog.controller;

import com.cinereserve.catalog.dto.ApiResponse;
import com.cinereserve.catalog.dto.LocationRequest;
import com.cinereserve.catalog.model.Location;
import com.cinereserve.catalog.security.UserContext;
import com.cinereserve.catalog.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    private void checkAdmin() {
        String role = UserContext.getRole();
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Access denied. Admin role required.");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Location>> createLocation(@Valid @RequestBody LocationRequest request) {
        checkAdmin();
        Location location = locationService.createLocation(request);
        return ResponseEntity.ok(ApiResponse.success(location, "Location created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Location>> updateLocation(@PathVariable String id, @Valid @RequestBody LocationRequest request) {
        checkAdmin();
        Location location = locationService.updateLocation(id, request);
        return ResponseEntity.ok(ApiResponse.success(location, "Location updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(@PathVariable String id) {
        checkAdmin();
        locationService.deleteLocation(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Location deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Location>>> getAllLocations() {
        List<Location> locations = locationService.getAllLocations();
        return ResponseEntity.ok(ApiResponse.success(locations, "Locations retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Location>> getLocationById(@PathVariable String id) {
        Location location = locationService.getLocationById(id);
        return ResponseEntity.ok(ApiResponse.success(location, "Location retrieved successfully"));
    }
}
