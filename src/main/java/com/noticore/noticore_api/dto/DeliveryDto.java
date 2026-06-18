package com.noticore.noticore_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeliveryDto {
    private String timestamp;
    private long processingTimeMillis;
    private List<String> recipients;
    private String smtpResponse;
    private String reportingMTA;
}
