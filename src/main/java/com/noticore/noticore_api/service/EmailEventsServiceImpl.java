package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.SesEventDto;
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
    public void handleEmailEvents(SesEventDto eventDto, String message) {
        EmailNotificationStatus status = resolveStatus(eventDto);

        if (status == null) {
            log.info("Ignoring event type: {}",
                    eventDto.getNotificationType() != null ? eventDto.getNotificationType() : eventDto.getEventType());
            return;
        }

        String messageId = eventDto.getMail().getMessageId();
        EmailNotifications emailNotifications = emailNotificationsRepository.findBySesMessageId(messageId).orElseThrow(
                () -> new AppException("Notification not found with ses message id: " + messageId, 404, LocalDateTime.now())
        );

        // 1. Save event to email_events
        iEmailEventsPersistenceService.addEmailEvent(status, message, eventDto);

        // 2. Update email_notifications status (skip for OPENED/CLICKED)
        if (status != EmailNotificationStatus.OPENED && status != EmailNotificationStatus.CLICKED) {
            iEmailNotificationsPersistenceService.updateEmailNotificationStatusBySesMessageId(emailNotifications, status);
        }

        // 3. Add to suppression list if needed
        if (status == EmailNotificationStatus.BOUNCED_HARD) {
            iSuppressedEmailsService.addSuppression(emailNotifications, EmailNotificationStatus.BOUNCED_HARD);
        } else if (status == EmailNotificationStatus.COMPLAINED) {
            iSuppressedEmailsService.addSuppression(emailNotifications, EmailNotificationStatus.COMPLAINED);
        }
    }

    private EmailNotificationStatus resolveStatus(SesEventDto event) {
        String type = event.getNotificationType() != null
                ? event.getNotificationType()
                : event.getEventType();

        return switch (type) {
            case "Delivery" -> EmailNotificationStatus.DELIVERED;
            case "Complaint" -> EmailNotificationStatus.COMPLAINED;
            case "Reject" -> EmailNotificationStatus.REJECTED;
            case "Open" -> EmailNotificationStatus.OPENED;
            case "Click" -> EmailNotificationStatus.CLICKED;
            case "Bounce" -> resolveBounceStatus(event);
            default -> null; // "Send" and unknown types — ignore
        };
    }

    private EmailNotificationStatus resolveBounceStatus(SesEventDto event) {
        if (event.getBounce() == null) return null;

        String bounceType = event.getBounce().getBounceType();
        if ("Permanent".equals(bounceType)) {
            return EmailNotificationStatus.BOUNCED_HARD;
        } else if ("Transient".equals(bounceType)) {
            return EmailNotificationStatus.BOUNCED_SOFT;
        }
        return null;
    }
}
