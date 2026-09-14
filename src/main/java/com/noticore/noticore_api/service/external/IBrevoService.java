package com.noticore.noticore_api.service.external;

import com.noticore.noticore_api.dto.DnsRecordDto;

import java.util.Set;

public interface IBrevoService {

    Set<DnsRecordDto> registerDomain(String domainName);
    boolean getDomainStatus(String domainName);
    String sendEmail(String from, String to, String subject, String body);
    void deleteDomain(String domainName);
}
