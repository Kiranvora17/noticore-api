package com.noticore.noticore_api.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.noticore.noticore_api.config.BrevoConfig;
import com.noticore.noticore_api.dto.DnsRecordDto;
import com.noticore.noticore_api.exception.brevo.BrevoConnectionException;
import com.noticore.noticore_api.exception.brevo.BrevoDomainNotFoundException;
import com.noticore.noticore_api.exception.brevo.DomainRegisterationException;
import com.noticore.noticore_api.exception.domain.DomainExistException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrevoServiceImpl implements IBrevoService {

    private static final String BASE_URL = "https://api.brevo.com/v3";

    private final RestTemplate restTemplate;
    private final BrevoConfig brevoConfig;
    private final JavaMailSender javaMailSender;

    @Override
    public Set<DnsRecordDto> registerDomain(String domainName) {
        log.info("Registering domain with Brevo: {}", domainName);

        HttpHeaders headers = buildHeaders();
        HttpEntity<Map<String, String>> createRequest = new HttpEntity<>(Map.of("name", domainName), headers);

        try {
            restTemplate.postForEntity(BASE_URL + "/senders/domains", createRequest, JsonNode.class);
        } catch (HttpClientErrorException e) {
            if (isDuplicateDomainError(e)) {
                log.warn("Domain {} is already registered with Brevo.", domainName);
                throw new DomainExistException(domainName);
            }
            log.error("Failed to register domain with Brevo: {}", e.getMessage());
            throw new DomainRegisterationException("Failed to register domain with Brevo: " + e.getMessage());
        } catch (RestClientException e) {
            log.error("Failed to register domain with Brevo: {}", e.getMessage());
            throw new DomainRegisterationException("Failed to register domain with Brevo: " + e.getMessage());
        }

        JsonNode domainDetails;
        try {
            HttpEntity<Void> getRequest = new HttpEntity<>(headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    BASE_URL + "/senders/domains/" + domainName,
                    HttpMethod.GET,
                    getRequest,
                    JsonNode.class
            );
            domainDetails = response.getBody();
        } catch (RestClientException e) {
            log.error("Domain created but failed to fetch DNS records from Brevo: {}", e.getMessage());
            throw new DomainRegisterationException("Domain created but failed to fetch DNS records from Brevo: " + e.getMessage());
        }

        return extractDnsRecords(domainDetails);
    }

    @Override
    public boolean getDomainStatus(String domainName) {
        log.info("Checking Brevo domain status for: {}", domainName);

        HttpEntity<Void> request = new HttpEntity<>(buildHeaders());

        JsonNode body;
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    BASE_URL + "/senders/domains/" + domainName,
                    HttpMethod.GET,
                    request,
                    JsonNode.class
            );
            body = response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new BrevoDomainNotFoundException(domainName);
        } catch (RestClientException e) {
            log.error("Failed to fetch domain status from Brevo: {}", e.getMessage());
            throw new BrevoConnectionException("Failed to fetch domain status from Brevo: " + e.getMessage());
        }

        if (body != null && body.path("authenticated").asBoolean(false)) {
            return true;
        }

        // Brevo does not authenticate a domain automatically once DNS matches -
        // it must be explicitly triggered via this endpoint (the same action the
        // dashboard's "Authenticate domain" button performs).
        try {
            restTemplate.exchange(
                    BASE_URL + "/senders/domains/" + domainName + "/authenticate",
                    HttpMethod.PUT,
                    request,
                    JsonNode.class
            );
            return true;
        } catch (HttpClientErrorException e) {
            log.info("Domain {} not ready to authenticate yet: {}", domainName, e.getMessage());
            return false;
        } catch (RestClientException e) {
            log.error("Failed to trigger authentication for domain {}: {}", domainName, e.getMessage());
            throw new BrevoConnectionException("Failed to trigger authentication for domain: " + e.getMessage());
        }
    }

    @Override
    public String sendEmail(String from, String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            String domain = StringUtils.substringAfter(from, "@");
            String generatedMessageId = "<" + UUID.randomUUID() + "@" + domain + ">";
            mimeMessage.setHeader("Message-ID", generatedMessageId);

            javaMailSender.send(mimeMessage);

            return generatedMessageId.replaceAll("[<>]", "");
        } catch (Exception e) {
            if (e instanceof org.springframework.mail.MailException mailException) {
                throw mailException;
            }
            throw new MailPreparationException("Failed to prepare email for sending via Brevo", e);
        }
    }

    @Override
    public void deleteDomain(String domainName) {
        log.info("Deleting domain from Brevo: {}", domainName);

        HttpEntity<Void> request = new HttpEntity<>(buildHeaders());

        try {
            restTemplate.exchange(
                    BASE_URL + "/senders/domains/" + domainName,
                    HttpMethod.DELETE,
                    request,
                    Void.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            // Already gone on Brevo's side - nothing left to do.
            log.warn("Domain {} was not found on Brevo when deleting; treating as already removed.", domainName);
        } catch (RestClientException e) {
            log.error("Failed to delete domain from Brevo: {}", e.getMessage());
            throw new BrevoConnectionException("Failed to delete domain from Brevo: " + e.getMessage());
        }
    }

    private boolean isDuplicateDomainError(HttpClientErrorException e) {
        String body = e.getResponseBodyAsString();
        return body != null && body.contains("duplicate_parameter");
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", brevoConfig.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private Set<DnsRecordDto> extractDnsRecords(JsonNode domainDetails) {
        Set<DnsRecordDto> records = new LinkedHashSet<>();
        if (domainDetails == null) {
            return records;
        }

        JsonNode dnsRecords = domainDetails.path("dns_records");

        addRecord(records, dnsRecords.path("brevo_code"), "TXT");
        addRecord(records, firstNonNullNode(dnsRecords, "dkim1Record", "dkim_record"), "CNAME");
        addRecord(records, firstNonNullNode(dnsRecords, "dkim2Record", "dkim2_record"), "CNAME");
        addRecord(records, dnsRecords.path("dmarc_record"), "TXT");

        return records;
    }

    private JsonNode firstNonNullNode(JsonNode parent, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode node = parent.path(fieldName);
            if (!node.isMissingNode() && !node.isNull()) {
                return node;
            }
        }
        return null;
    }

    private void addRecord(Set<DnsRecordDto> records, JsonNode node, String defaultType) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return;
        }

        String host = firstNonBlank(node, "host_name", "hostname", "host");
        String value = firstNonBlank(node, "value", "target");

        if (StringUtils.isBlank(host) && StringUtils.isBlank(value)) {
            return;
        }

        DnsRecordDto dto = new DnsRecordDto();
        dto.setType(StringUtils.defaultIfBlank(node.path("type").asText(null), defaultType).toUpperCase());
        dto.setHost(host);
        dto.setValue(value);
        records.add(dto);
    }

    private String firstNonBlank(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            String value = node.path(fieldName).asText(null);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }
}
