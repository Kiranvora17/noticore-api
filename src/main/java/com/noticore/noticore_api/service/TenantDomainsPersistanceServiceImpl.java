package com.noticore.noticore_api.service;

import com.noticore.noticore_api.converter.TenantDomainsConverter;
import com.noticore.noticore_api.converter.TenantsConverter;
import com.noticore.noticore_api.dto.DnsRecordDto;
import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.entity.TenantDomains;
import com.noticore.noticore_api.entity.Tenants;
import com.noticore.noticore_api.enums.DomainStatus;
import com.noticore.noticore_api.repository.TenantDomainsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.ses.model.IdentityDkimAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantDomainsPersistanceServiceImpl implements ITenantDomainsPersistenceService {

    private final TenantsConverter tenantsConverter;
    private final TenantDomainsRepository tenantDomainsRepository;
    private final TenantDomainsConverter tenantDomainsConverter;

    @Transactional
    public DomainResponseDto saveDomain(
            TenantsDto tenantsDto,
            String domainName,
            Set<DnsRecordDto> dnsRecords
    ) {

        Tenants tenants = tenantsConverter.convertToEntity(tenantsDto);

        TenantDomains tenantDomains = new TenantDomains();
        tenantDomains.setTenants(tenants);
        tenantDomains.setDomainName(domainName);
        tenantDomains.setDnsRecords(dnsRecords);
        tenantDomains.setStatus(DomainStatus.PENDING);

        TenantDomains saved = tenantDomainsRepository.save(tenantDomains);
        tenantDomainsRepository.flush();

        return tenantDomainsConverter.convertToDto(saved);
    }

    @Override
    @Transactional
    public void updateDomainVerificationStatus(List<TenantDomains> domains, Map<String, IdentityDkimAttributes> attributes) {
        for(TenantDomains domain: domains) {

            String domainName = domain.getDomainName();

            IdentityDkimAttributes dkimAttributes = attributes.get(domainName);

            if(dkimAttributes == null) {
                log.info("No dkim records found for the domain: {}", domainName);
                continue;
            }

            String verificationStatus = dkimAttributes.dkimVerificationStatusAsString();

            log.info("Domain name: {}, DKIM status: {}", domainName, verificationStatus);

            switch (verificationStatus) {
                case "Success":
                    domain.setStatus(DomainStatus.VERIFIED);
                    break;
                case "Failed":
                    domain.setStatus(DomainStatus.FAILED);
                    break;
            }
        }

        tenantDomainsRepository.saveAll(domains);
    }
}
