package com.noticore.noticore_api.dto;

import lombok.Data;

@Data
public class OpenDto {
    private String timestamp;
    private String ipAddress;
    private String userAgent;
}
