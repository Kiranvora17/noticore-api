package com.noticore.noticore_api.service;

import com.noticore.noticore_api.converter.EmailNotificationsConverter;
import com.noticore.noticore_api.converter.TenantsConverter;
import com.noticore.noticore_api.dto.*;
import com.noticore.noticore_api.entity.EmailNotifications;
import com.noticore.noticore_api.entity.TenantDomains;
import com.noticore.noticore_api.entity.Tenants;
import com.noticore.noticore_api.enums.DomainStatus;
import com.noticore.noticore_api.enums.EmailNotificationStatus;
import com.noticore.noticore_api.enums.NotificationAttemptStatus;
import com.noticore.noticore_api.exception.domain.DomainNotVerifiedException;
import com.noticore.noticore_api.exception.domain.InvalidDomainException;
import com.noticore.noticore_api.exception.email.InvalidEmailException;
import com.noticore.noticore_api.exception.email.SuppressedEmailException;
import com.noticore.noticore_api.exception.notification.NotificationNotFoundException;
import com.noticore.noticore_api.rabbitMQ.EmailNotificationProducer;
import com.noticore.noticore_api.repository.EmailNotificationsRepository;
import com.noticore.noticore_api.repository.SuppressedEmailsRespository;
import com.noticore.noticore_api.service.external.IBrevoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final ITenantDomainsService iTenantDomainsService;
    private final IBrevoService iBrevoService;
    private final INotificationAttemptsPersistentService iNotificationAttemptsPersistentService;
    private final IEmailNotificationsPersistenceService iEmailNotificationsPersistenceService;
    private final EmailNotificationProducer emailNotificationProducer;
    private final EmailNotificationsRepository emailNotificationsRepository;
    private final EmailNotificationsConverter emailNotificationsConverter;
    private final SuppressedEmailsRespository suppressedEmailsRespository;
    private final TenantsConverter tenantsConverter;

    private final EmailValidator emailValidator;
    private final DomainValidator domainValidator;

    @Override
    public SendEmailResponseDto sendEmail(TenantsDto tenantsDto, SendEmailRequestDto sendEmailRequestDto) {
        log.info("inside method: sendEmail()");
        Tenants tenants = tenantsConverter.convertToEntity(tenantsDto);

        String from = sendEmailRequestDto.getFrom();
        String to = sendEmailRequestDto.getTo();

        log.info("Validating email and domain...");
        if(!emailValidator.isValid(from)) {
            throw new InvalidEmailException(from);
        }

        if(!emailValidator.isValid(to)) {
            throw new InvalidEmailException(to);
        }

        String domain = StringUtils.substringAfter(from, "@");

        if(!domainValidator.isValid(domain)) {
            throw new InvalidDomainException(domain);
        }

        boolean isSuppressed = suppressedEmailsRespository.existsByTenants_IdAndEmail(tenants.getId(), sendEmailRequestDto.getTo());

        if(isSuppressed) {
            throw new SuppressedEmailException(sendEmailRequestDto.getTo());
        }

        TenantDomains tenantDomain = iTenantDomainsService.getDomainEntityByName(domain);
        DomainStatus status = tenantDomain.getStatus();

        if(!status.equals(DomainStatus.VERIFIED)) {
            throw new DomainNotVerifiedException(domain);
        }

        log.info("email and domain validation completed successfully.");
        log.info("Saving email notification data...");
        EmailNotifications emailNotification = iEmailNotificationsPersistenceService
                .saveEmailNotification(tenants, sendEmailRequestDto, tenantDomain);

        SendEmailResponseDto res = new SendEmailResponseDto();
        res.setId(emailNotification.getId());
        res.setStatus(emailNotification.getEmailNotificationStatus());
        res.setTimeStamp(LocalDateTime.now());

        log.info("publishing the notification to the queue.");
        emailNotificationProducer.publishToMainQueue(emailNotification.getId());

        log.info("notification is successfully published in the queue.");
        log.info("method executed successfully: sendEmail()");
        return res;
    }

    @Override
    public void processEmail(UUID id) {
        log.info("recieved notification id, id: {}", id);

        EmailNotifications emailNotifications = emailNotificationsRepository
                .findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        log.info("updating the status of notification with id: {}, to processing", id);
        iEmailNotificationsPersistenceService.updateEmailNotificationStatus(
                emailNotifications,
                EmailNotificationStatus.PROCESSING,
                null
        );

        try {
            log.info("calling brevo to send email...");
            String messageId = iBrevoService.sendEmail(
                    emailNotifications.getFromEmail(),
                    emailNotifications.getToEmail(),
                    emailNotifications.getSubject(),
                    emailNotifications.getBody()
            );
            log.info("brevo call was successfull, storing the provider message id...");
            iEmailNotificationsPersistenceService.addProviderMessageId(emailNotifications, messageId);
            log.info("email attempts stored successfully");
        } catch (MailAuthenticationException e) {
            // permanent - SMTP credentials/config issue, not retryable
            log.info("mail authentication exception occurred. marking the status to permanently failed.");
            iEmailNotificationsPersistenceService.updateEmailNotificationStatus(
                    emailNotifications,
                    EmailNotificationStatus.PERMANENTLY_FAILED,
                    e.getMessage()
            );
            iNotificationAttemptsPersistentService
                    .saveAttempt(
                            emailNotifications,
                            NotificationAttemptStatus.FAILED,
                            e.getMessage()
                    );
        } catch (MailSendException e) {
            if (isPermanentSmtpFailure(e)) {
                log.info("permanent smtp failure occurred. marking the status to permanently failed.");
                iEmailNotificationsPersistenceService.updateEmailNotificationStatus(
                        emailNotifications,
                        EmailNotificationStatus.PERMANENTLY_FAILED,
                        e.getMessage()
                );
                iNotificationAttemptsPersistentService
                        .saveAttempt(
                                emailNotifications,
                                NotificationAttemptStatus.FAILED,
                                e.getMessage()
                        );
            } else {
                log.info("transient smtp failure occurred. publishing notification to retry queue.");
                retrySendEmail(emailNotifications);
            }
        } catch (MailException e) {
            // connectivity or other transient issue - retry
            log.info("mail exception occurred. publishing notification to retry queue.");
            retrySendEmail(emailNotifications);
        }
    }

    private boolean isPermanentSmtpFailure(MailSendException e) {
        for (Exception cause : e.getMessageExceptions()) {
            String message = cause.getMessage();
            if (message != null && message.matches("(?s).*\\b5\\d{2}\\b.*")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void retrySendEmail(EmailNotifications emailNotifications) {
        int retryCount = emailNotifications.getRetryCount();

        if(retryCount >= 3) {
            iEmailNotificationsPersistenceService.updateEmailNotificationStatus(
                    emailNotifications,
                    EmailNotificationStatus.PERMANENTLY_FAILED,
                    "Maximum retry reached"
            );
            return;
        }

        emailNotificationProducer.publishToRetryQueue(emailNotifications.getId());
        iEmailNotificationsPersistenceService.updateRetryCount(emailNotifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailNotificationsDto> getAll(TenantsDto tenantsDto) {
        Optional<List<EmailNotifications>> notifications = emailNotificationsRepository
                .findAllByTenants_Id(tenantsDto.getId());

        if(notifications.isPresent()) {
            return notifications.get().stream().map(emailNotificationsConverter::covertToDto).toList();
        }

        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public EmailNotificationsDto getEmailNotification(TenantsDto tenantsDto, UUID notificationId) {
        EmailNotifications notification = emailNotificationsRepository
                .findByIdAndTenants_Id(notificationId, tenantsDto.getId())
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        return emailNotificationsConverter.covertToDto(notification);
    }
}
