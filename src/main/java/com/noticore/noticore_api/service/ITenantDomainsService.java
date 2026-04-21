package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.DomainRequestDto;
import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;

public interface ITenantDomainsService {

    DomainResponseDto registerDomain(DomainRequestDto request, TenantsDto tenant);
}
