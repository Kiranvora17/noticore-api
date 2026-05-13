package com.noticore.noticore_api.exception.notification;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationNotFoundException extends AppException {

    public NotificationNotFoundException(UUID id) {
        super("Notification does not exist with id: {}" + id, 404, LocalDateTime.now());
    }
}
