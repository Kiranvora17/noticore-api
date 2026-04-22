package com.noticore.noticore_api.dto;

import com.noticore.noticore_api.enums.DomainStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomainResponseDto {
    private UUID id;
    private String domainName;
    private Set<DnsRecordDto> dnsRecords;
    private DomainStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;
}
