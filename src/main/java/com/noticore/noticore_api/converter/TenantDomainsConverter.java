package com.noticore.noticore_api.converter;

import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.entity.TenantDomains;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantDomainsConverter {

    private final ModelMapper modelMapper;

    public DomainResponseDto convertToDto(TenantDomains tenantDomains) {
        return modelMapper.map(tenantDomains, DomainResponseDto.class);
    }

    public TenantDomains convertToEntity(DomainResponseDto domainResponseDto) {
        return modelMapper.map(domainResponseDto, TenantDomains.class);
    }
}
