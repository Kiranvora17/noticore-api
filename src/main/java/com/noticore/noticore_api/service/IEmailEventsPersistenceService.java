package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.SesEventDto;
import com.noticore.noticore_api.enums.EmailNotificationStatus;

public interface IEmailEventsPersistenceService {
    void addEmailEvent(EmailNotificationStatus status, String payload, SesEventDto sesEventDto);
}
