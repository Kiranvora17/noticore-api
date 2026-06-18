package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.*;
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
    public void addEmailEvent(EmailNotificationStatus status, String payload, SesEventDto sesEventDto) {
        String messageId = sesEventDto.getMail().getMessageId();

        Optional<EmailNotifications> emailNotifications = emailNotificationsRepository.findBySesMessageId(messageId);

        if(!emailNotifications.isPresent()) {
            throw new AppException("email notification not found with message id: "+ messageId , 404, LocalDateTime.now());
        }

        Map<String, String> metadata = extractMetadata(sesEventDto, status);

        EmailEvents emailEvents = new EmailEvents();

        emailEvents.setId(UUID.randomUUID());
        emailEvents.setEventType(status.toString());
        emailEvents.setEmailNotifications(emailNotifications.get());
        emailEvents.setOccurredAt(LocalDateTime.now());
        emailEvents.setPayload(payload);
        emailEvents.setMetadata(metadata);

        emailEventsRepository.save(emailEvents);
    }

    private Map<String, String> extractMetadata(SesEventDto event, EmailNotificationStatus status) {
        Map<String, String> metadata = new HashMap<>();

        switch (status) {
            case DELIVERED -> {
                DeliveryDto delivery = event.getDelivery();
                if (delivery != null) {
                    metadata.put("processingTimeMillis", String.valueOf(delivery.getProcessingTimeMillis()));
                    metadata.put("smtpResponse", delivery.getSmtpResponse());
                }
            }

            case BOUNCED_HARD, BOUNCED_SOFT -> {
                BounceDto bounce = event.getBounce();
                if (bounce != null) {
                    metadata.put("bounceType", bounce.getBounceType());
                    metadata.put("bounceSubType", bounce.getBounceSubType());
                    if (bounce.getBouncedRecipients() != null && !bounce.getBouncedRecipients().isEmpty()) {
                        metadata.put("diagnosticCode", bounce.getBouncedRecipients().get(0).getDiagnosticCode());
                    }
                }
            }

            case COMPLAINED -> {
                ComplaintDto complaint = event.getComplaint();
                if (complaint != null) {
                    metadata.put("complaintFeedbackType", complaint.getComplaintFeedbackType());
                }
            }

            case REJECTED -> {
                metadata.put("reason", "Bad content");
            }

            case OPENED -> {
                OpenDto open = event.getOpen();
                if (open != null) {
                    metadata.put("ipAddress", open.getIpAddress());
                    metadata.put("userAgent", open.getUserAgent());
                }
            }

            case CLICKED -> {
                ClickDto click = event.getClick();
                if (click != null) {
                    metadata.put("ipAddress", click.getIpAddress());
                    metadata.put("userAgent", click.getUserAgent());
                    metadata.put("link", click.getLink());
                }
            }

            default -> log.warn("No metadata extraction defined for event type: {}", status);
        }

        return metadata;
    }
}
