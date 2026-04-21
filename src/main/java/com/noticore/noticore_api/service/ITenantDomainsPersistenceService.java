package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;

import java.util.List;

public interface ITenantDomainsPersistenceService {

    DomainResponseDto saveDomain(TenantsDto tenantsDto, String domainName, List<String> dkimTokens);
}
