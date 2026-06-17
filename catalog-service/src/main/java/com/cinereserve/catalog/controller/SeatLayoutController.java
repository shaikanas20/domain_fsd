package com.cinereserve.catalog.controller;

import com.cinereserve.catalog.dto.ApiResponse;
import com.cinereserve.catalog.dto.SeatLayoutRequest;
import com.cinereserve.catalog.model.SeatLayout;
import com.cinereserve.catalog.security.UserContext;
import com.cinereserve.catalog.service.SeatLayoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seatlayouts")
public class SeatLayoutController {

    private final SeatLayoutService seatLayoutService;

    public SeatLayoutController(SeatLayoutService seatLayoutService) {
        this.seatLayoutService = seatLayoutService;
    }

    private void checkAdminOrOwner() {
        String role = UserContext.getRole();
        if (!"ADMIN".equals(role) && !"THEATRE_OWNER".equals(role)) {
            throw new RuntimeException("Access denied. Admin or Theatre Owner role required.");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SeatLayout>> createSeatLayout(@Valid @RequestBody SeatLayoutRequest request) {
        checkAdminOrOwner();
        SeatLayout seatLayout = seatLayoutService.createSeatLayout(request);
        return ResponseEntity.ok(ApiResponse.success(seatLayout, "Seat layout created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatLayout>> getSeatLayoutById(@PathVariable String id) {
        SeatLayout seatLayout = seatLayoutService.getSeatLayoutById(id);
        return ResponseEntity.ok(ApiResponse.success(seatLayout, "Seat layout retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatLayout>>> getAllSeatLayouts() {
        List<SeatLayout> layouts = seatLayoutService.getAllSeatLayouts();
        return ResponseEntity.ok(ApiResponse.success(layouts, "Seat layouts retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSeatLayout(@PathVariable String id) {
        checkAdminOrOwner();
        seatLayoutService.deleteSeatLayout(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Seat layout deleted successfully"));
    }
}
