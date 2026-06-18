package com.noticore.noticore_api.dto;

import lombok.Data;

@Data
public class MailDto {
    private String messageId;
    private String timestamp;
    private String source;
}
