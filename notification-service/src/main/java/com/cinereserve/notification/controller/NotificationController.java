package com.cinereserve.notification.controller;

import com.cinereserve.notification.dto.ApiResponse;
import com.cinereserve.notification.model.Notification;
import com.cinereserve.notification.security.UserContext;
import com.cinereserve.notification.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotificationsForUser() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("Unauthorized context. User identity missing.");
        }
        List<Notification> history = notificationRepository.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(history, "Notification history retrieved successfully."));
    }
}
