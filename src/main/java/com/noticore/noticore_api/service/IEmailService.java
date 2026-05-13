package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.EmailNotificationsDto;
import com.noticore.noticore_api.dto.SendEmailRequestDto;
import com.noticore.noticore_api.dto.SendEmailResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.entity.EmailNotifications;

import java.util.List;
import java.util.UUID;

public interface IEmailService {
    SendEmailResponseDto sendEmail(TenantsDto tenantsDto, SendEmailRequestDto sendEmailRequestDto);
    void processEmail(UUID notificationId);
    void retrySendEmail(EmailNotifications emailNotifications);
    List<EmailNotificationsDto> getAll(TenantsDto tenantsDto);
    EmailNotificationsDto getEmailNotification(UUID notificationId);
}
