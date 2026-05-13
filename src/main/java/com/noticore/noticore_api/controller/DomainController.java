package com.noticore.noticore_api.controller;

import com.noticore.noticore_api.dto.DomainRequestDto;
import com.noticore.noticore_api.dto.DomainResponseDto;
import com.noticore.noticore_api.dto.TenantsDto;
import com.noticore.noticore_api.service.ITenantDomainsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/domains")
@RequiredArgsConstructor
public class DomainController {

    private final ITenantDomainsService iTenantDomainsService;

    @PostMapping
    public ResponseEntity<DomainResponseDto> register(
            @Valid @RequestBody DomainRequestDto domainRequestDto,
            HttpServletRequest httpServletRequest
            ) {

        TenantsDto tenant = (TenantsDto) httpServletRequest.getAttribute("tenant");
        DomainResponseDto response = iTenantDomainsService.registerDomain(domainRequestDto, tenant);
        return ResponseEntity.status(201).body(response);

    }

    @GetMapping
    public ResponseEntity<List<DomainResponseDto>> getAllDomains(
            HttpServletRequest httpServletRequest
    ) {
        TenantsDto tenant = (TenantsDto) httpServletRequest.getAttribute("tenant");
        List<DomainResponseDto> response = iTenantDomainsService.getAllDomains(tenant);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DomainResponseDto> getDomain(
            @PathVariable UUID id,
            HttpServletRequest httpServletRequest
            ) {
        TenantsDto tenantsDto = (TenantsDto) httpServletRequest.getAttribute("tenant");
        DomainResponseDto response = iTenantDomainsService.getDomainDto(tenantsDto, id);
        return ResponseEntity.ok(response);
    }

}
