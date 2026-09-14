package com.noticore.noticore_api.service;

import com.noticore.noticore_api.converter.TenantDomainsConverter;
import com.noticore.noticore_api.dto.DnsRecordDto;
import com.noticore.noticore_api.dto.DomainRequestDto;
import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.entity.TenantDomains;
import com.noticore.noticore_api.exception.domain.DomainExistException;
import com.noticore.noticore_api.exception.domain.DomainInUseException;
import com.noticore.noticore_api.exception.domain.DomainNotFoundException;
import com.noticore.noticore_api.exception.domain.InvalidDomainException;
import com.noticore.noticore_api.repository.EmailNotificationsRepository;
import com.noticore.noticore_api.repository.TenantDomainsRepository;
import com.noticore.noticore_api.service.external.IBrevoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.DomainValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantDomainsServiceImpl implements ITenantDomainsService {

    private final IBrevoService iBrevoService;
    private final ITenantDomainsPersistenceService iTenantDomainsPersistenceService;
    private final TenantDomainsRepository tenantDomainsRepository;
    private final EmailNotificationsRepository emailNotificationsRepository;
    private final TenantDomainsConverter tenantDomainsConverter;

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

        // Brevo has one shared list of sender domains for the whole account, so a
        // domain already claimed by a different tenant can never be verified by
        // this one. Same exception/message as the same-tenant case so we don't
        // leak whether the domain belongs to another customer.
        boolean claimedByOtherTenant = tenantDomainsRepository
                .existsByDomainNameAndTenants_IdNot(domainName, tenantDto.getId());

        if (claimedByOtherTenant) {
            throw new DomainExistException(domainName);
        }

        Set<DnsRecordDto> dnsRecords = iBrevoService.registerDomain(domainName);

        return iTenantDomainsPersistenceService
                .saveDomain(tenantDto, domainName, dnsRecords);
    }

    @Override
    public List<DomainResponseDto> getAllDomains(TenantsDto tenantsDto) {
        List<TenantDomains> tenantDomains = tenantDomainsRepository
                .findAllByTenants_Id(tenantsDto.getId());

        return tenantDomains
                .stream()
                .map(tenantDomainsConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DomainResponseDto getDomainDto(TenantsDto tenantsDto, UUID domainId) {
        TenantDomains tenantDomains = tenantDomainsRepository
                .findByIdAndTenants_Id(domainId, tenantsDto.getId())
                .orElseThrow(() -> new DomainNotFoundException(domainId));

        return tenantDomainsConverter.convertToDto(tenantDomains);
    }

    @Override
    public TenantDomains getDomainEntityByName(String domainName) {

        return tenantDomainsRepository
                .findByDomainName(domainName)
                .orElseThrow(() -> new DomainNotFoundException(domainName));
    }

    @Override
    @Transactional
    public void deleteDomain(TenantsDto tenantsDto, UUID domainId) {
        TenantDomains domain = tenantDomainsRepository
                .findByIdAndTenants_Id(domainId, tenantsDto.getId())
                .orElseThrow(() -> new DomainNotFoundException(domainId));

        // Deleting a domain that has already been used to send email would
        // orphan that send history's foreign key - block it rather than lose
        // or dangle those records. Domains that never sent anything (e.g.
        // failed/abandoned verification) are always safe to remove.
        if (emailNotificationsRepository.existsByTenantDomains_Id(domainId)) {
            throw new DomainInUseException(domain.getDomainName());
        }

        iBrevoService.deleteDomain(domain.getDomainName());
        tenantDomainsRepository.delete(domain);
    }
}
