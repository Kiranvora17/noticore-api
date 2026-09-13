package com.noticore.noticore_api.service.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noticore.noticore_api.config.BrevoConfig;
import com.noticore.noticore_api.dto.BrevoEventDto;
import com.noticore.noticore_api.exception.base.AppException;
import com.noticore.noticore_api.service.IEmailEventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrevoWebhookServiceImpl implements IBrevoWebhookService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final BrevoConfig brevoConfig;
    private final ObjectMapper objectMapper;
    private final IEmailEventsService iEmailEventsService;

    @Override
    public void handle(String payload, String signature) {
        if (!isValidSignature(payload, signature)) {
            log.warn("Invalid Brevo webhook signature received.");
            throw new AppException("Invalid webhook signature", 401, LocalDateTime.now());
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

    private boolean isValidSignature(String payload, String signature) {
        if (signature == null || signature.isBlank()) {
            return false;
        }

        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(brevoConfig.getWebhookSecret().getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] computed = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computedHex = HexFormat.of().formatHex(computed);

            return MessageDigest.isEqual(
                    computedHex.getBytes(StandardCharsets.UTF_8),
                    signature.trim().getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            log.error("Error validating Brevo webhook signature: {}", e.getMessage(), e);
            return false;
        }
    }
}
