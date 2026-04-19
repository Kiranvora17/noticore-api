package com.noticore.noticore_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TenantsDto {
    private UUID id;
    private String rapidapiUsername;
    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;
}
