package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.BrevoEventDto;
import com.noticore.noticore_api.entity.EmailNotifications;
import com.noticore.noticore_api.enums.EmailNotificationStatus;

public interface IEmailEventsPersistenceService {
    void addEmailEvent(EmailNotifications emailNotifications, EmailNotificationStatus status, String payload, BrevoEventDto brevoEventDto);
}
