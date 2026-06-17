package com.cinereserve.booking.controller;

import com.cinereserve.booking.dto.ApiResponse;
import com.cinereserve.booking.dto.BookingRequest;
import com.cinereserve.booking.dto.BookingResponse;
import com.cinereserve.booking.dto.ShowSeatResponse;
import com.cinereserve.booking.security.UserContext;
import com.cinereserve.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private Long getRequiredUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("Unauthorized transaction. User context missing.");
        }
        return userId;
    }

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<BookingResponse>> initiateBooking(@Valid @RequestBody BookingRequest request) {
        Long userId = getRequiredUserId();
        BookingResponse response = bookingService.initiateBooking(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Booking initiated, seats locked for 5 minutes."));
    }

    @PostMapping("/confirm/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> confirmBooking(@PathVariable Long bookingId) {
        Long userId = getRequiredUserId();
        BookingResponse response = bookingService.confirmBooking(userId, bookingId);
        return ResponseEntity.ok(ApiResponse.success(response, "Booking confirmed successfully."));
    }

    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(@PathVariable Long bookingId) {
        Long userId = getRequiredUserId();
        BookingResponse response = bookingService.cancelBooking(userId, bookingId);
        return ResponseEntity.ok(ApiResponse.success(response, "Booking cancelled successfully."));
    }

    @GetMapping("/shows/{showId}/seats")
    public ResponseEntity<ApiResponse<List<ShowSeatResponse>>> getSeatAvailability(@PathVariable Long showId) {
        List<ShowSeatResponse> response = bookingService.getSeatAvailability(showId);
        return ResponseEntity.ok(ApiResponse.success(response, "Seat availability retrieved successfully."));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingHistory() {
        Long userId = getRequiredUserId();
        List<BookingResponse> responses = bookingService.getBookingHistory(userId);
        return ResponseEntity.ok(ApiResponse.success(responses, "User booking history retrieved."));
    }
}
