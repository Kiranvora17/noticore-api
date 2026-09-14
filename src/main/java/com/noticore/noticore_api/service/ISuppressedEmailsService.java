package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.SuppressedEmailRequestDto;
import com.noticore.noticore_api.dto.SuppressedEmailResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.entity.EmailNotifications;
import com.noticore.noticore_api.entity.Tenants;
import com.noticore.noticore_api.enums.EmailNotificationStatus;

import java.util.List;

public interface ISuppressedEmailsService {
    void addSuppression(EmailNotifications emailNotifications, EmailNotificationStatus status);
    SuppressedEmailResponseDto addManualSuppression(TenantsDto tenantsDto, SuppressedEmailRequestDto request);
    SuppressedEmailResponseDto removeSuppression(TenantsDto tenantsDto, String email);
    List<SuppressedEmailResponseDto> getAllSuppressions(TenantsDto tenantsDto);
}
