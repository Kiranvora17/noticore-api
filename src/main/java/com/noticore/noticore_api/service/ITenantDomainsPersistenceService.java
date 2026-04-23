package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.DnsRecordDto;
import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.entity.TenantDomains;
import software.amazon.awssdk.services.ses.model.IdentityDkimAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ITenantDomainsPersistenceService {

    DomainResponseDto saveDomain(TenantsDto tenantsDto, String domainName, Set<DnsRecordDto> dnsRecords);
    void updateDomainVerificationStatus(List<TenantDomains> domains, Map<String, IdentityDkimAttributes> attributes);
}
