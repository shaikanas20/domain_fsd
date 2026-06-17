package com.cinereserve.notification;

import com.cinereserve.notification.consumer.BookingEventConsumer;
import com.cinereserve.notification.event.BookingEvent;
import com.cinereserve.notification.model.Notification;
import com.cinereserve.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BookingEventConsumerTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private BookingEventConsumer eventConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsumeBookingConfirmed_Success() {
        BookingEvent event = new BookingEvent(
                100L, 1L, 10L, 250.0, 
                Collections.singletonList("A1"), "CONFIRMED", LocalDateTime.now()
        );

        eventConsumer.consumeBookingConfirmed(event);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testConsumeBookingCancelled_Success() {
        BookingEvent event = new BookingEvent(
                100L, 1L, 10L, 250.0, 
                Collections.singletonList("A1"), "CANCELLED", LocalDateTime.now()
        );

        eventConsumer.consumeBookingCancelled(event);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
