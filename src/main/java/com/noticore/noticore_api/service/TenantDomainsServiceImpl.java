package com.noticore.noticore_api.service;

import com.noticore.noticore_api.dto.DnsRecordDto;
import com.noticore.noticore_api.dto.DomainRequestDto;
import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.exception.domain.DomainExistException;
import com.noticore.noticore_api.exception.domain.InvalidDomainException;
import com.noticore.noticore_api.repository.TenantDomainsRepository;
import com.noticore.noticore_api.service.external.ISesService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.DomainValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TenantDomainsServiceImpl implements ITenantDomainsService {

    private final ISesService iSesService;
    private final ITenantDomainsPersistenceService iTenantDomainsPersistenceService;
    private final TenantDomainsRepository tenantDomainsRepository;

    @Override
    public DomainResponseDto registerDomain(DomainRequestDto request, TenantsDto tenantDto) {
        String domainName = request.getDomainName().trim().toLowerCase();

        DomainValidator validator = DomainValidator.getInstance();
        if(!validator.isValid(domainName)) {
            throw new InvalidDomainException(domainName);
        }

        boolean domainExists = tenantDomainsRepository
                .existsByDomainNameAndTenants_Id(domainName, tenantDto.getId());

        if(domainExists) {
            throw new DomainExistException(domainName);
        }

        Set<DnsRecordDto> dnsRecords = iSesService.registerDomain(domainName);

        return iTenantDomainsPersistenceService
                .saveDomain(tenantDto, domainName, dnsRecords);
    }
}
