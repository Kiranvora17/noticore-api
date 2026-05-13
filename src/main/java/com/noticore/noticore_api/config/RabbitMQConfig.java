package com.noticore.noticore_api.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String MAIN_EXCHANGE = "noticore.email.exchange";
    public static final String DLX_EXCHANGE = "noticore.email.dlx.exchange";

    public static final String MAIN_QUEUE = "noticore.email.queue";
    public static final String RETRY_QUEUE = "noticore.email.retry.queue";

    public static final String ROUTING_KEY = "email.send";

    public static final int RETRY_TTL = 300000;

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(MAIN_EXCHANGE);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue mainQueue() {
        return QueueBuilder
                .durable(MAIN_QUEUE)
                .build();
    }

    @Bean
    public Queue retryQueue() {
        return QueueBuilder
                .durable(RETRY_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY)
                .withArgument("x-message-ttl", RETRY_TTL)
                .build();
    }

    @Bean
    public Binding mainBinding(Queue mainQueue, DirectExchange mainExchange) {
        return BindingBuilder
                .bind(mainQueue)
                .to(mainExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding dlxBinding(Queue mainQueue, DirectExchange dlxExchange) {
        return BindingBuilder
                .bind(mainQueue)
                .to(dlxExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
