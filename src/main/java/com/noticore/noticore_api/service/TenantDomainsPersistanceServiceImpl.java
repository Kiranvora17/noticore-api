package com.noticore.noticore_api.service;

import com.noticore.noticore_api.converter.TenantDomainsConverter;
import com.noticore.noticore_api.converter.TenantsConverter;
import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.entity.TenantDomains;
import com.noticore.noticore_api.entity.Tenants;
import com.noticore.noticore_api.enums.DomainStatus;
import com.noticore.noticore_api.repository.TenantDomainsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantDomainsPersistanceServiceImpl implements ITenantDomainsPersistenceService {

    private final TenantsConverter tenantsConverter;
    private final TenantDomainsRepository tenantDomainsRepository;
    private final TenantDomainsConverter tenantDomainsConverter;

    @Transactional
    public DomainResponseDto saveDomain(
            TenantsDto tenantsDto,
            String domainName,
            List<String> dkimTokens
    ) {

        Tenants tenants = tenantsConverter.convertToEntity(tenantsDto);

        TenantDomains tenantDomains = new TenantDomains();
        tenantDomains.setTenants(tenants);
        tenantDomains.setDomainName(domainName);
        tenantDomains.setDkimToken(String.join(",", dkimTokens));
        tenantDomains.setStatus(DomainStatus.PENDING);

        TenantDomains saved = tenantDomainsRepository.save(tenantDomains);

        return tenantDomainsConverter.convertToDto(saved);
    }
}
