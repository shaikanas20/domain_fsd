package com.cinereserve.booking.event;

import com.cinereserve.booking.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookingEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public BookingEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendBookingConfirmed(BookingEvent event) {
        log.info("Publishing Booking Confirmed event to RabbitMQ for booking ID: {}", event.bookingId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.CONFIRMED_ROUTING_KEY, event);
    }

    public void sendBookingCancelled(BookingEvent event) {
        log.info("Publishing Booking Cancelled event to RabbitMQ for booking ID: {}", event.bookingId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.CANCELLED_ROUTING_KEY, event);
    }
}
