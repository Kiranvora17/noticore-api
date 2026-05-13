package com.noticore.noticore_api.service;

import com.noticore.noticore_api.entity.EmailNotifications;
import com.noticore.noticore_api.enums.NotificationAttemptStatus;

import java.util.UUID;

public interface INotificationAttemptsPersistentService {

    void saveAttempt(EmailNotifications emailNotifications, NotificationAttemptStatus status, String errorMessage);
}
