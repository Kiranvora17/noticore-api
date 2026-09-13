package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.BrevoEventDto;
import com.noticore.noticore_api.entity.EmailEvents;
import com.noticore.noticore_api.entity.EmailNotifications;
import com.noticore.noticore_api.enums.EmailNotificationStatus;
import com.noticore.noticore_api.exception.base.AppException;
import com.noticore.noticore_api.repository.EmailEventsRepository;
import com.noticore.noticore_api.repository.EmailNotificationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailEventsPersistenceServiceImpl implements IEmailEventsPersistenceService {

    private final EmailNotificationsRepository emailNotificationsRepository;
    private final EmailEventsRepository emailEventsRepository;

    @Override
    @Transactional
    public void addEmailEvent(EmailNotificationStatus status, String payload, BrevoEventDto brevoEventDto) {
        String messageId = brevoEventDto.getMessageId();

        Optional<EmailNotifications> emailNotifications = emailNotificationsRepository.findByProviderMessageId(messageId);

        if(!emailNotifications.isPresent()) {
            throw new AppException("email notification not found with message id: "+ messageId , 404, LocalDateTime.now());
        }

        Map<String, String> metadata = extractMetadata(brevoEventDto, status);

        EmailEvents emailEvents = new EmailEvents();

        emailEvents.setId(UUID.randomUUID());
        emailEvents.setEventType(status.toString());
        emailEvents.setEmailNotifications(emailNotifications.get());
        emailEvents.setOccurredAt(LocalDateTime.now());
        emailEvents.setPayload(payload);
        emailEvents.setMetadata(metadata);

        emailEventsRepository.save(emailEvents);
    }

    private Map<String, String> extractMetadata(BrevoEventDto event, EmailNotificationStatus status) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("provider", "brevo");

        switch (status) {
            case DELIVERED -> metadata.put("messageId", event.getMessageId());

            case BOUNCED_HARD, BOUNCED_SOFT -> {
                metadata.put("reason", event.getReason());
                metadata.put("messageId", event.getMessageId());
            }

            case COMPLAINED -> metadata.put("messageId", event.getMessageId());

            case REJECTED -> {
                metadata.put("reason", event.getReason());
                metadata.put("messageId", event.getMessageId());
            }

            case OPENED -> {
                metadata.put("ipAddress", event.getIp());
                metadata.put("userAgent", event.getUserAgent());
            }

            case CLICKED -> {
                metadata.put("link", event.getLink());
                metadata.put("ipAddress", event.getIp());
            }

            default -> log.warn("No metadata extraction defined for event type: {}", status);
        }

        return metadata;
    }
}
