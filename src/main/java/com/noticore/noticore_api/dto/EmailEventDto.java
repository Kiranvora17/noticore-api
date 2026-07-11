package com.noticore.noticore_api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class EmailEventDto {
    private UUID id;
    private String eventType;
    private LocalDateTime occurredAt;
    private Map<String, String> metadata;
}
