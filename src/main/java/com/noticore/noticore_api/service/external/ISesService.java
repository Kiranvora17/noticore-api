package com.noticore.noticore_api.service.external;

import com.noticore.noticore_api.dto.DnsRecordDto;

import java.util.Set;

public interface ISesService {

    Set<DnsRecordDto> registerDomain(String domainName);
}
