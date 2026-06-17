package com.cinereserve.notification.consumer;

import com.cinereserve.notification.event.BookingEvent;
import com.cinereserve.notification.model.Notification;
import com.cinereserve.notification.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookingEventConsumer {

    private final NotificationRepository notificationRepository;

    public BookingEventConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @RabbitListener(queues = "notification.booking.confirmed")
    public void consumeBookingConfirmed(BookingEvent event) {
        log.info("Received Booking Confirmed Event for ID: {}", event.bookingId());

        String message = String.format("Dear User, your booking (ID: %d) for Show ID %d is CONFIRMED. " +
                "Seats: %s. Total Paid: $%.2f. Enjoy your movie!",
                event.bookingId(), event.showId(), event.seatNumbers(), event.totalAmount());

        // Mock Email
        log.info("[MOCK EMAIL SENT] to User ID: {} -> Content: {}", event.userId(), message);
        // Mock SMS
        log.info("[MOCK SMS SENT] to User ID: {} -> Content: {}", event.userId(), message);

        // Save notification log to database
        Notification notification = Notification.builder()
                .userId(event.userId())
                .bookingId(event.bookingId())
                .message(message)
                .notificationType("EMAIL_AND_SMS")
                .build();

        notificationRepository.save(notification);
    }

    @RabbitListener(queues = "notification.booking.cancelled")
    public void consumeBookingCancelled(BookingEvent event) {
        log.info("Received Booking Cancelled Event for ID: {}", event.bookingId());

        String message = String.format("Dear User, your booking (ID: %d) has been CANCELLED. " +
                "Seats released: %s. Refund of $%.2f has been initiated.",
                event.bookingId(), event.seatNumbers(), event.totalAmount());

        // Mock Email
        log.info("[MOCK EMAIL SENT] to User ID: {} -> Content: {}", event.userId(), message);
        // Mock SMS
        log.info("[MOCK SMS SENT] to User ID: {} -> Content: {}", event.userId(), message);

        // Save notification log to database
        Notification notification = Notification.builder()
                .userId(event.userId())
                .bookingId(event.bookingId())
                .message(message)
                .notificationType("EMAIL_AND_SMS")
                .build();

        notificationRepository.save(notification);
    }
}
