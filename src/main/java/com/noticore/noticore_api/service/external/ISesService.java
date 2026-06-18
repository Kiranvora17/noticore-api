package com.noticore.noticore_api.service.external;

import com.noticore.noticore_api.dto.DnsRecordDto;
import com.noticore.noticore_api.dto.SendEmailRequestDto;
import software.amazon.awssdk.services.ses.model.IdentityDkimAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ISesService {

    Set<DnsRecordDto> registerDomain(String domainName);
    Map<String, IdentityDkimAttributes> getDkimStatus(List<String> domainName);
    String sendEmail(SendEmailRequestDto sendEmailRequestDto);
}
