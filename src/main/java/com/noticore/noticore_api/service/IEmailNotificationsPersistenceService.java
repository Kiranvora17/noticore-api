package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.SendEmailRequestDto;
import com.noticore.noticore_api.entity.EmailNotifications;
import com.noticore.noticore_api.entity.TenantDomains;
import com.noticore.noticore_api.entity.Tenants;
import com.noticore.noticore_api.enums.EmailNotificationStatus;

public interface IEmailNotificationsPersistenceService {

    EmailNotifications saveEmailNotification(Tenants tenants, SendEmailRequestDto sendEmailRequestDto, TenantDomains tenantDomains);
    void updateEmailNotificationStatus(EmailNotifications emailNotifications, EmailNotificationStatus status, String errorMessage);
    void updateEmailNotificationStatusBySesMessageId(EmailNotifications emailNotifications, EmailNotificationStatus status);
    void updateRetryCount(EmailNotifications emailNotifications);
    void addSesMessageId(EmailNotifications emailNotifications, String sesMessageId);
}
