    package com.noticore.noticore_api.service;

    import com.noticore.noticore_api.dto.SendEmailRequestDto;
    import com.noticore.noticore_api.entity.EmailNotifications;
    import com.noticore.noticore_api.entity.TenantDomains;
    import com.noticore.noticore_api.entity.Tenants;
    import com.noticore.noticore_api.enums.EmailNotificationStatus;
    import com.noticore.noticore_api.exception.base.AppException;
    import com.noticore.noticore_api.repository.EmailNotificationsRepository;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDateTime;
    import java.util.Optional;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class EmailNotificationsPersistenceServiceImpl implements IEmailNotificationsPersistenceService {

        private final EmailNotificationsRepository emailNotificationsRepository;

        @Override
        @Transactional
        public EmailNotifications saveEmailNotification(
                Tenants tenants,
                SendEmailRequestDto sendEmailRequestDto,
                TenantDomains tenantDomains) {

            log.info("inside method: saveEmailNotification()");

            EmailNotifications emailNotifications = new EmailNotifications();

            emailNotifications.setTenants(tenants);
            emailNotifications.setFromEmail(sendEmailRequestDto.getFrom());
            emailNotifications.setToEmail(sendEmailRequestDto.getTo());
            emailNotifications.setSubject(sendEmailRequestDto.getSubject());
            emailNotifications.setBody(sendEmailRequestDto.getBody());
            emailNotifications.setTenantDomains(tenantDomains);
            emailNotifications.setEmailNotificationStatus(EmailNotificationStatus.QUEUED);
            emailNotifications.setRetryCount(0);

            EmailNotifications response = emailNotificationsRepository.save(emailNotifications);
            log.info("email notification saved successfully in the db.");
            log.info("method executed successfully: saveEmailNotification()");
            return response;
        }

        @Override
        @Transactional
        public void updateEmailNotificationStatus(
                EmailNotifications emailNotifications,
                EmailNotificationStatus status,
                String errorMessage) {

            emailNotifications.setEmailNotificationStatus(status);
            if(errorMessage != null && !errorMessage.isEmpty()) {
                emailNotifications.setErrorMessage(errorMessage);
            }
            emailNotificationsRepository.save(emailNotifications);
        }

        @Override
        @Transactional
        public void updateEmailNotificationStatusBySesMessageId(
                EmailNotifications emailNotifications,
                EmailNotificationStatus status
        ) {

            emailNotifications.setEmailNotificationStatus(status);
            emailNotificationsRepository.save(emailNotifications);
        }

        @Override
        @Transactional
        public void addSesMessageId(EmailNotifications emailNotifications, String sesMessageId) {
            emailNotifications.setSesMessageId(sesMessageId);
            emailNotificationsRepository.save(emailNotifications);
        }

        @Override
        @Transactional
        public void updateRetryCount(EmailNotifications emailNotifications) {
            int currentRetryCount = emailNotifications.getRetryCount();

            emailNotifications.setRetryCount(currentRetryCount+1);
            emailNotificationsRepository.save(emailNotifications);
        }
    }
