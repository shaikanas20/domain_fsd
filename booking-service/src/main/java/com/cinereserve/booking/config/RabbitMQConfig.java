package com.cinereserve.booking.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "booking.exchange";
    public static final String CONFIRMED_QUEUE = "notification.booking.confirmed";
    public static final String CANCELLED_QUEUE = "notification.booking.cancelled";
    public static final String CONFIRMED_ROUTING_KEY = "booking.confirmed";
    public static final String CANCELLED_ROUTING_KEY = "booking.cancelled";

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue confirmedQueue() {
        return new Queue(CONFIRMED_QUEUE, true);
    }

    @Bean
    public Queue cancelledQueue() {
        return new Queue(CANCELLED_QUEUE, true);
    }

    @Bean
    public Binding confirmedBinding() {
        return BindingBuilder.bind(confirmedQueue())
                .to(bookingExchange())
                .with(CONFIRMED_ROUTING_KEY);
    }

    @Bean
    public Binding cancelledBinding() {
        return BindingBuilder.bind(cancelledQueue())
                .to(bookingExchange())
                .with(CANCELLED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
