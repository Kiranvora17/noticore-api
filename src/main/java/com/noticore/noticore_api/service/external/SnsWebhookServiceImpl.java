package com.noticore.noticore_api.service.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noticore.noticore_api.dto.SesEventDto;
import com.noticore.noticore_api.exception.base.AppException;
import com.noticore.noticore_api.service.IEmailEventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnsWebhookServiceImpl implements ISnsWebhookService{

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final IEmailEventsService iEmailEventsService;

    @Override
    public void handle(String messageType, String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);

            switch (messageType) {
                case "SubscriptionConfirmation" -> handleConfirmation(root);
                case "Notification" -> handleNotification(root);
                default -> log.warn("Unknown SNS message type: {}", messageType);
            }
        } catch (Exception e) {
            log.error("Error processing SNS event: {}", e.getMessage(), e);
        }
    }

    private void handleConfirmation(JsonNode root) {
        String subscribeUrl = root.get("SubscribeURL").asText();
        log.info("Confirming SNS subscription. URL: {}", subscribeUrl);
        restTemplate.getForObject(subscribeUrl, String.class);
        log.info("SNS subscription confirmed successfully");
    }

    private void handleNotification(JsonNode root) {
        String message = root.get("Message").asText();
        log.info("SNS notification received: {}", message);
        // we will parse and handle this next
        try {
            SesEventDto event = objectMapper.readValue(message, SesEventDto.class);
            iEmailEventsService.handleEmailEvents(event, message);
        } catch (JsonProcessingException e) {
            log.error("Error processing the notification event: {}", message);
        }

    }
}
