package com.noticore.noticore_api.dto;

import com.noticore.noticore_api.enums.DomainStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class TenantDomainsDto {
    private UUID id;
    private TenantsDto tenants;
    private String domainName;
    private Set<DnsRecordDto> dnsRecords;
    private DomainStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;
}
