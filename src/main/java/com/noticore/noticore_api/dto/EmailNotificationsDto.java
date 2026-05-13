package com.noticore.noticore_api.dto;

import com.noticore.noticore_api.enums.EmailNotificationStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class EmailNotificationsDto {
    private UUID id;
    private TenantsDto tenants;
    private TenantDomainsDto tenantDomains;
    private String fromEmail;
    private String toEmail;
    private String subject;
    private String body;
    private EmailNotificationStatus emailNotificationStatus;
    private int retryCount;
    private String errorMessage;
    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;
    private List<NotificationAttemptsDto> notificationAttempts;
}
