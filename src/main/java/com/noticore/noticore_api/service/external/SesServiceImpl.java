package com.noticore.noticore_api.service.external;

import com.noticore.noticore_api.exception.ses.AWSConnectionException;
import com.noticore.noticore_api.exception.ses.DomainRegisterationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SesException;
import software.amazon.awssdk.services.ses.model.VerifyDomainDkimRequest;
import software.amazon.awssdk.services.ses.model.VerifyDomainDkimResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SesServiceImpl implements ISesService {

    private final SesClient sesClient;

    @Override
    public List<String> registerDomain(String domainName) {

        try {
            VerifyDomainDkimResponse response = sesClient
                    .verifyDomainDkim(VerifyDomainDkimRequest
                            .builder()
                            .domain(domainName)
                            .build());

            return response.dkimTokens();
        } catch (SesException s) {
            throw new DomainRegisterationException(s.getMessage());
        } catch (Exception e) {
            throw new AWSConnectionException(e.getMessage());
        }
    }
}
