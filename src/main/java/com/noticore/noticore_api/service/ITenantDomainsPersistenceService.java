package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.DnsRecordDto;
import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;

import java.util.List;
import java.util.Set;

public interface ITenantDomainsPersistenceService {

    DomainResponseDto saveDomain(TenantsDto tenantsDto, String domainName, Set<DnsRecordDto> dnsRecords);
}
