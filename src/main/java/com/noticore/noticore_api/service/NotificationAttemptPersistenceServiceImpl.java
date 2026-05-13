package com.noticore.noticore_api.service;

import com.noticore.noticore_api.entity.EmailNotifications;
import com.noticore.noticore_api.entity.NotificationAttempts;
import com.noticore.noticore_api.enums.NotificationAttemptStatus;
import com.noticore.noticore_api.repository.NotificationAttemptsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationAttemptPersistenceServiceImpl implements INotificationAttemptsPersistentService {

    private final NotificationAttemptsRepository notificationAttemptsRepository;

    @Override
    @Transactional
    public void saveAttempt(EmailNotifications emailNotifications, NotificationAttemptStatus status, String errorMessage) {

        NotificationAttempts notificationAttempts = new NotificationAttempts();

        notificationAttempts.setEmailNotifications(emailNotifications);
        notificationAttempts.setAttemptedAt(LocalDateTime.now());
        notificationAttempts.setStatus(status);
        notificationAttempts.setErrorMessage(errorMessage);

        notificationAttemptsRepository.save(notificationAttempts);
    }
}
