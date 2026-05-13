package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.DomainRequestDto;
import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.entity.TenantDomains;

import java.util.List;
import java.util.UUID;

public interface ITenantDomainsService {

    DomainResponseDto registerDomain(DomainRequestDto request, TenantsDto tenant);
    List<DomainResponseDto> getAllDomains(TenantsDto tenantsDto);
    DomainResponseDto getDomainDto(TenantsDto tenantsDto, UUID domainId);
    TenantDomains getDomainEntityByName(String domainName);
}
