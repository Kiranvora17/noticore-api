package com.noticore.noticore_api.service.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noticore.noticore_api.config.BrevoConfig;
import com.noticore.noticore_api.dto.BrevoEventDto;
import com.noticore.noticore_api.exception.base.AppException;
import com.noticore.noticore_api.service.IEmailEventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrevoWebhookServiceImpl implements IBrevoWebhookService {

    private static final String BASIC_PREFIX = "Basic ";

    private final BrevoConfig brevoConfig;
    private final ObjectMapper objectMapper;
    private final IEmailEventsService iEmailEventsService;

    @Override
    public void handle(String payload, String authorizationHeader) {
        if (!isValidAuth(authorizationHeader)) {
            log.warn("Invalid or missing Brevo webhook credentials.");
            throw new AppException("Invalid webhook credentials", 401, LocalDateTime.now());
        }

        try {
            BrevoEventDto event = objectMapper.readValue(payload, BrevoEventDto.class);
            log.info("Brevo webhook event received: {}", event.getEvent());
            iEmailEventsService.handleEmailEvents(event, payload);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error processing Brevo webhook payload: {}", e.getMessage(), e);
            throw new AppException("Invalid webhook payload", 400, LocalDateTime.now());
        }
    }

    /**
     * Brevo does not sign webhook payloads. Authentication is configured as
     * "Basic" auth with a dedicated username/password on the webhook itself
     * (Transactional email > Settings > Webhook), which Brevo then sends as a
     * standard Authorization header on every delivery.
     */
    private boolean isValidAuth(String authorizationHeader) {
        // The auth-scheme token ("Basic") is case-insensitive per RFC 7235.
        // Brevo sends it lowercase ("basic ..."), so this must not be a
        // case-sensitive prefix check.
        if (authorizationHeader == null
                || !authorizationHeader.regionMatches(true, 0, BASIC_PREFIX, 0, BASIC_PREFIX.length())) {
            return false;
        }

        String expectedUsername = brevoConfig.getWebhookUsername();
        String expectedPassword = brevoConfig.getWebhookPassword();

        if (expectedUsername == null || expectedUsername.isBlank()
                || expectedPassword == null || expectedPassword.isBlank()) {
            log.error("Brevo webhook username/password is not configured.");
            return false;
        }

        try {
            String encoded = authorizationHeader.substring(BASIC_PREFIX.length());
            String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);

            int separatorIndex = decoded.indexOf(':');
            if (separatorIndex < 0) {
                return false;
            }

            String username = decoded.substring(0, separatorIndex);
            String password = decoded.substring(separatorIndex + 1);

            return MessageDigest.isEqual(
                    username.getBytes(StandardCharsets.UTF_8),
                    expectedUsername.getBytes(StandardCharsets.UTF_8)
            ) && MessageDigest.isEqual(
                    password.getBytes(StandardCharsets.UTF_8),
                    expectedPassword.getBytes(StandardCharsets.UTF_8)
            );
        } catch (IllegalArgumentException e) {
            log.error("Error decoding Brevo webhook Authorization header: {}", e.getMessage(), e);
            return false;
        }
    }
}
