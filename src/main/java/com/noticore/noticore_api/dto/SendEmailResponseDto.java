package com.noticore.noticore_api.dto;

import com.noticore.noticore_api.enums.EmailNotificationStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SendEmailResponseDto {
    private UUID id;
    private EmailNotificationStatus status;
    private LocalDateTime timeStamp;
}
