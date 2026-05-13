package com.noticore.noticore_api.dto;

import com.noticore.noticore_api.enums.NotificationAttemptStatus;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationAttemptsDto {
    private UUID id;
    private EmailNotificationsDto emailNotifications;
    private LocalDateTime attemptedAt;
    private NotificationAttemptStatus status;
    private String errorMessage;
    private LocalDateTime creationDate;
    private LocalDateTime modiefiedDate;
}
