package com.noticore.noticore_api.controller;

import com.noticore.noticore_api.dto.DomainRequestDto;
import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.service.ITenantDomainsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/domains")
@RequiredArgsConstructor
public class DomainController {

    private final ITenantDomainsService iTenantDomainsService;

    @PostMapping
    public DomainResponseDto register(
            @Valid @RequestBody DomainRequestDto domainRequestDto,
            HttpServletRequest httpServletRequest
            ) {

        TenantsDto tenant = (TenantsDto) httpServletRequest.getAttribute("tenant");
        return iTenantDomainsService.registerDomain(domainRequestDto, tenant);

    }
}
