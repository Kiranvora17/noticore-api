package com.noticore.noticore_api.service;

import com.noticore.noticore_api.converter.SuppressedEmailsConverter;
import com.noticore.noticore_api.converter.TenantsConverter;
import com.noticore.noticore_api.dto.OpenDto;
import com.noticore.noticore_api.dto.SesEventDto;
import com.noticore.noticore_api.dto.SuppressedEmailRequestDto;
import com.noticore.noticore_api.dto.SuppressedEmailResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.entity.EmailNotifications;
import com.noticore.noticore_api.entity.SuppressedEmails;
import com.noticore.noticore_api.entity.Tenants;
import com.noticore.noticore_api.enums.EmailNotificationStatus;
import com.noticore.noticore_api.exception.email.InvalidEmailException;
import com.noticore.noticore_api.exception.email.SuppressedEmailExistException;
import com.noticore.noticore_api.exception.email.SuppressedEmailNotFoundException;
import com.noticore.noticore_api.repository.SuppressedEmailsRespository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuppressedEmailsServiceImpl implements ISuppressedEmailsService{

    private static final String MANUAL_SUPPRESSION_REASON = "MANUAL";

    private final SuppressedEmailsRespository suppressedEmailsRespository;
    private final SuppressedEmailsConverter suppressedEmailsConverter;
    private final TenantsConverter tenantsConverter;
    private final EmailValidator emailValidator;

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

    @Override
    @Transactional
    public SuppressedEmailResponseDto addManualSuppression(TenantsDto tenantsDto, SuppressedEmailRequestDto request) {
        String email = request.getEmail().trim().toLowerCase();

        if(!emailValidator.isValid(email)) {
            throw new InvalidEmailException(email);
        }

        if(suppressedEmailsRespository.existsByTenants_IdAndEmail(tenantsDto.getId(), email)) {
            throw new SuppressedEmailExistException(email);
        }

        Tenants tenants = tenantsConverter.convertToEntity(tenantsDto);

        SuppressedEmails suppressedEmails = new SuppressedEmails();
        suppressedEmails.setTenants(tenants);
        suppressedEmails.setEmail(email);
        suppressedEmails.setReason(
                request.getReason() != null && !request.getReason().isBlank()
                        ? request.getReason()
                        : MANUAL_SUPPRESSION_REASON
        );

        SuppressedEmails saved = suppressedEmailsRespository.save(suppressedEmails);
        return suppressedEmailsConverter.convertToDto(saved);
    }

    @Override
    @Transactional
    public void removeSuppression(TenantsDto tenantsDto, String email) {
        String normalizedEmail = email.trim().toLowerCase();

        suppressedEmailsRespository.findByTenants_IdAndEmail(tenantsDto.getId(), normalizedEmail)
                .orElseThrow(() -> new SuppressedEmailNotFoundException(normalizedEmail));

        suppressedEmailsRespository.deleteByTenants_IdAndEmail(tenantsDto.getId(), normalizedEmail);
    }
}
