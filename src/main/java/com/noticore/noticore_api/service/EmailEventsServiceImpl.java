package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.BrevoEventDto;
import com.noticore.noticore_api.entity.EmailNotifications;
import com.noticore.noticore_api.enums.EmailNotificationStatus;
import com.noticore.noticore_api.exception.base.AppException;
import com.noticore.noticore_api.repository.EmailNotificationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailEventsServiceImpl implements IEmailEventsService {

    private final IEmailEventsPersistenceService iEmailEventsPersistenceService;
    private final IEmailNotificationsPersistenceService iEmailNotificationsPersistenceService;
    private final ISuppressedEmailsService iSuppressedEmailsService;
    private final EmailNotificationsRepository emailNotificationsRepository;

    @Override
    public void handleEmailEvents(BrevoEventDto eventDto, String message) {
        EmailNotificationStatus status = resolveStatus(eventDto);

        if (status == null) {
            log.info("Ignoring event type: {}", eventDto.getEvent());
            return;
        }

        String messageId = eventDto.getMessageId();
        EmailNotifications emailNotifications = emailNotificationsRepository.findByProviderMessageId(messageId).orElseThrow(
                () -> new AppException("Notification not found with provider message id: " + messageId, 404, LocalDateTime.now())
        );

        // 1. Save event to email_events
        iEmailEventsPersistenceService.addEmailEvent(status, message, eventDto);

        // 2. Update email_notifications status (skip for OPENED/CLICKED)
        if (status != EmailNotificationStatus.OPENED && status != EmailNotificationStatus.CLICKED) {
            iEmailNotificationsPersistenceService.updateEmailNotificationStatusByProviderMessageId(emailNotifications, status);
        }

        // 3. Add to suppression list if needed
        if (status == EmailNotificationStatus.BOUNCED_HARD) {
            iSuppressedEmailsService.addSuppression(emailNotifications, EmailNotificationStatus.BOUNCED_HARD);
        } else if (status == EmailNotificationStatus.COMPLAINED) {
            iSuppressedEmailsService.addSuppression(emailNotifications, EmailNotificationStatus.COMPLAINED);
        }
    }

    private EmailNotificationStatus resolveStatus(BrevoEventDto event) {
        String type = event.getEvent();

        if (type == null) {
            return null;
        }

        return switch (type) {
            case "delivered" -> EmailNotificationStatus.DELIVERED;
            case "hard_bounce" -> EmailNotificationStatus.BOUNCED_HARD;
            case "soft_bounce" -> EmailNotificationStatus.BOUNCED_SOFT;
            case "spam" -> EmailNotificationStatus.COMPLAINED;
            case "invalid_email", "blocked" -> EmailNotificationStatus.REJECTED;
            case "opened" -> EmailNotificationStatus.OPENED;
            case "click" -> EmailNotificationStatus.CLICKED;
            default -> null; // "request", "deferred", "unsubscribed", etc. — ignore
        };
    }
}
