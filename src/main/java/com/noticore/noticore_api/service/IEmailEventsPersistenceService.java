package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.BrevoEventDto;
import com.noticore.noticore_api.enums.EmailNotificationStatus;

public interface IEmailEventsPersistenceService {
    void addEmailEvent(EmailNotificationStatus status, String payload, BrevoEventDto brevoEventDto);
}
