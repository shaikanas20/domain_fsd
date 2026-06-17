package com.cinereserve.notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CONFIRMED_QUEUE = "notification.booking.confirmed";
    public static final String CANCELLED_QUEUE = "notification.booking.cancelled";

    @Bean
    public Queue confirmedQueue() {
        return new Queue(CONFIRMED_QUEUE, true);
    }

    @Bean
    public Queue cancelledQueue() {
        return new Queue(CANCELLED_QUEUE, true);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
