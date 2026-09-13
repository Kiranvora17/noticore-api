package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.DnsRecordDto;
import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.entity.TenantDomains;
import com.noticore.noticore_api.enums.DomainStatus;

import java.util.Set;

public interface ITenantDomainsPersistenceService {

    DomainResponseDto saveDomain(TenantsDto tenantsDto, String domainName, Set<DnsRecordDto> dnsRecords);
    void updateDomainVerificationStatus(TenantDomains domain, DomainStatus newStatus);
}
