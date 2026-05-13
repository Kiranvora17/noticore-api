package com.noticore.noticore_api.service.external;

import com.noticore.noticore_api.dto.DnsRecordDto;
import com.noticore.noticore_api.dto.SendEmailRequestDto;
import com.noticore.noticore_api.exception.ses.AWSConnectionException;
import com.noticore.noticore_api.exception.ses.DomainRegisterationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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

    @Override
    public Map<String, IdentityDkimAttributes> getDkimStatus(List<String> domainNames) {

        try {
            GetIdentityDkimAttributesResponse res = sesClient
                    .getIdentityDkimAttributes(
                            GetIdentityDkimAttributesRequest
                                    .builder()
                                    .identities(domainNames)
                                    .build()
                    );
            return res.dkimAttributes();

        } catch (SesException s) {
            throw new AWSConnectionException(s.getMessage());
        }
    }

    @Override
    public void sendEmail(SendEmailRequestDto sendEmailRequestDto) throws SesException {
        Destination destination = Destination.builder()
                .toAddresses(sendEmailRequestDto.getTo())
                .build();

        Content content = Content.builder()
                .data(sendEmailRequestDto.getBody())
                .build();

        Content sub = Content.builder()
                .data(sendEmailRequestDto.getSubject())
                .build();

        Body body = Body.builder()
                .html(content)
                .build();

        Message msg = Message.builder()
                .subject(sub)
                .body(body)
                .build();

        SendEmailRequest emailRequest = SendEmailRequest
                .builder()
                .destination(destination)
                .message(msg)
                .source(sendEmailRequestDto.getFrom())
                .build();

        sesClient.sendEmail(emailRequest);
    }
}
