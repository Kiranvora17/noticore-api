package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.SesEventDto;
import com.noticore.noticore_api.entity.EmailNotifications;
import com.noticore.noticore_api.entity.Tenants;
import com.noticore.noticore_api.enums.EmailNotificationStatus;

public interface ISuppressedEmailsService {
    void addSuppression(EmailNotifications emailNotifications, EmailNotificationStatus status);
}
