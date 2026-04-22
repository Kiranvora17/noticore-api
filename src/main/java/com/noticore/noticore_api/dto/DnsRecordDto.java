package com.noticore.noticore_api.dto;

import lombok.Data;

@Data
public class DnsRecordDto {
    private String type;
    private String name;
    private String value;
}
