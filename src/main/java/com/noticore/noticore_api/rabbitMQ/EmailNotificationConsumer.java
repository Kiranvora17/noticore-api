package com.noticore.noticore_api.rabbitMQ;

import com.noticore.noticore_api.config.RabbitMQConfig;
import com.noticore.noticore_api.converter.EmailNotificationsConverter;
import com.noticore.noticore_api.dto.EmailNotificationsDto;
import com.noticore.noticore_api.entity.EmailNotifications;
import com.noticore.noticore_api.enums.EmailNotificationStatus;
import com.noticore.noticore_api.enums.NotificationAttemptStatus;
import com.noticore.noticore_api.exception.notification.NotificationNotFoundException;
import com.noticore.noticore_api.repository.EmailNotificationsRepository;
import com.noticore.noticore_api.service.IEmailNotificationsPersistenceService;
import com.noticore.noticore_api.service.IEmailService;
import com.noticore.noticore_api.service.INotificationAttemptsPersistentService;
import com.noticore.noticore_api.service.external.ISesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationConsumer {

    private final ISesService iSesService;
    private final IEmailNotificationsPersistenceService iEmailNotificationsPersistenceService;
    private final INotificationAttemptsPersistentService iNotificationAttemptsPersistentService;
    private final IEmailService iEmailService;
    private final EmailNotificationsRepository emailNotificationsRepository;
    private final EmailNotificationsConverter emailNotificationsConverter;

    @RabbitListener(queues = RabbitMQConfig.MAIN_QUEUE)
    public void recieveEmailEvent(String notificationId) {
        UUID id = UUID.fromString(notificationId);
        iEmailService.processEmail(id);
    }
}
