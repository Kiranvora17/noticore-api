package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.OpenDto;
import com.noticore.noticore_api.dto.SesEventDto;
import com.noticore.noticore_api.entity.EmailNotifications;
import com.noticore.noticore_api.entity.SuppressedEmails;
import com.noticore.noticore_api.entity.Tenants;
import com.noticore.noticore_api.enums.EmailNotificationStatus;
import com.noticore.noticore_api.repository.SuppressedEmailsRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuppressedEmailsServiceImpl implements ISuppressedEmailsService{

    private final SuppressedEmailsRespository suppressedEmailsRespository;

    @Override
    @Transactional
    public void addSuppression(EmailNotifications emailNotifications, EmailNotificationStatus status) {
        UUID tenantId = emailNotifications.getTenants().getId();
        String email = emailNotifications.getToEmail();

        Optional<SuppressedEmails> existing = suppressedEmailsRespository.findByTenants_IdAndEmail(tenantId, email);

        if(existing.isPresent()) {
            SuppressedEmails suppressedEmails = existing.get();
            suppressedEmails.setReason(status.toString());
            suppressedEmails.setModifiedDate(LocalDateTime.now());
            suppressedEmailsRespository.save(suppressedEmails);
        } else {
            SuppressedEmails suppressedEmails = new SuppressedEmails();
            suppressedEmails.setId(UUID.randomUUID());
            suppressedEmails.setTenants(emailNotifications.getTenants());
            suppressedEmails.setEmail(email);
            suppressedEmails.setReason(status.toString());
            suppressedEmails.setCreationDate(LocalDateTime.now());
            suppressedEmails.setModifiedDate(LocalDateTime.now());
            suppressedEmailsRespository.save(suppressedEmails);
        }
    }
}
