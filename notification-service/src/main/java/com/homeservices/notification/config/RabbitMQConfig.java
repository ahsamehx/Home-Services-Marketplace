package com.homeservices.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String BOOKING_EXCHANGE = "booking.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    public static final String BOOKING_CONFIRMED_NOTIFICATION_QUEUE = "notification.booking.confirmed";
    public static final String BOOKING_REJECTED_NOTIFICATION_QUEUE = "notification.booking.rejected";
    public static final String PAYMENT_FAILED_NOTIFICATION_QUEUE = "notification.payment.failed";

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(BOOKING_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue bookingConfirmedNotificationQueue() {
        return new Queue(BOOKING_CONFIRMED_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Queue bookingRejectedNotificationQueue() {
        return new Queue(BOOKING_REJECTED_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Queue paymentFailedNotificationQueue() {
        return new Queue(PAYMENT_FAILED_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding bookingConfirmedNotificationBinding(
            Queue bookingConfirmedNotificationQueue,
            TopicExchange bookingExchange) {
        return BindingBuilder.bind(bookingConfirmedNotificationQueue)
                .to(bookingExchange)
                .with("booking.confirmed");
    }

    @Bean
    public Binding bookingRejectedNotificationBinding(
            Queue bookingRejectedNotificationQueue,
            TopicExchange bookingExchange) {
        return BindingBuilder.bind(bookingRejectedNotificationQueue)
                .to(bookingExchange)
                .with("booking.rejected");
    }

    @Bean
    public Binding paymentFailedNotificationBinding(
            Queue paymentFailedNotificationQueue,
            DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentFailedNotificationQueue)
                .to(paymentExchange)
                .with("payment.failed");
    }
}