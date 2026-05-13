package com.noticore.noticore_api.rabbitMQ;

import com.noticore.noticore_api.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationProducer {

    private final AmqpTemplate amqpTemplate;

    public void publishToMainQueue(UUID notificationId) {
        log.info("publishing notification id: {}", notificationId);
        amqpTemplate.convertAndSend(
                RabbitMQConfig.MAIN_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                notificationId.toString()
        );
        log.info("Successfully published notification id: {}", notificationId);
    }

    public void publishToRetryQueue(UUID notificationId) {
        log.info("publishing notification to re retry queue with notification id: {}", notificationId);
        amqpTemplate.convertAndSend(
                RabbitMQConfig.RETRY_QUEUE,
                notificationId.toString()
        );
        log.info("Successfully publish notification id: {} to retry queue", notificationId);
    }
}
