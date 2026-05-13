package com.noticore.noticore_api.dto;

import lombok.Data;

@Data
public class SendEmailRequestDto {
    private String from;
    private String to;
    private String subject;
    private String body;
}
