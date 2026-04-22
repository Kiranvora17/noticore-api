package com.noticore.noticore_api.service.external;

import com.noticore.noticore_api.dto.DnsRecordDto;
import com.noticore.noticore_api.exception.ses.AWSConnectionException;
import com.noticore.noticore_api.exception.ses.DomainRegisterationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SesException;
import software.amazon.awssdk.services.ses.model.VerifyDomainDkimRequest;
import software.amazon.awssdk.services.ses.model.VerifyDomainDkimResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SesServiceImpl implements ISesService {

    private final SesClient sesClient;

    @Override
    public Set<DnsRecordDto> registerDomain(String domainName) {

        try {
            VerifyDomainDkimResponse response = sesClient
                    .verifyDomainDkim(VerifyDomainDkimRequest
                            .builder()
                            .domain(domainName)
                            .build());

            Set<DnsRecordDto> dnsRecordDtos = response.dkimTokens().stream().map((String token) -> {
                DnsRecordDto dnsRecordDto = new DnsRecordDto();
                dnsRecordDto.setName(token + "._domainkey." + domainName);
                dnsRecordDto.setValue(token + ".dkim.amazonses.com");
                dnsRecordDto.setType("CNAME");

                return dnsRecordDto;
            }).collect(Collectors.toSet());

            return dnsRecordDtos;

        } catch (SesException s) {
            throw new DomainRegisterationException(s.getMessage());
        } catch (Exception e) {
            throw new AWSConnectionException(e.getMessage());
        }
    }
}
