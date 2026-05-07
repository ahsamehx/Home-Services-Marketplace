package com.homeservices.booking.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String BOOKING_EXCHANGE = "booking.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(BOOKING_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE, true, false);
    }

    @Bean
    public FanoutExchange notificationExchange() {
        return new FanoutExchange(NOTIFICATION_EXCHANGE, true, false);
    }

    @Bean
    public Queue bookingQueue() {
        return new Queue("booking.queue", true);
    }

    @Bean
    public Queue bookingConfirmedQueue() {
        return new Queue("booking.confirmed", true);
    }

    @Bean
    public Queue bookingRejectedQueue() {
        return new Queue("booking.rejected", true);
    }

    @Bean
    public Queue paymentFailedQueue() {
        return new Queue("payment.failed", true);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue("notification.queue", true);
    }

    @Bean
    public Binding bookingBinding(Queue bookingQueue, TopicExchange bookingExchange) {
        return BindingBuilder.bind(bookingQueue).to(bookingExchange).with("booking.*");
    }

    @Bean
    public Binding bookingConfirmedBinding(Queue bookingConfirmedQueue, TopicExchange bookingExchange) {
        return BindingBuilder.bind(bookingConfirmedQueue).to(bookingExchange).with("booking.confirmed");
    }

    @Bean
    public Binding bookingRejectedBinding(Queue bookingRejectedQueue, TopicExchange bookingExchange) {
        return BindingBuilder.bind(bookingRejectedQueue).to(bookingExchange).with("booking.rejected");
    }

    @Bean
    public Binding paymentFailedBinding(Queue paymentFailedQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentFailedQueue).to(paymentExchange).with("payment.failed");
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, FanoutExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange);
    }
}
