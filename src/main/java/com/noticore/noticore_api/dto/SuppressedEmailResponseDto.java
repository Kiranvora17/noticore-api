package com.noticore.noticore_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuppressedEmailResponseDto {
    private UUID id;
    private String email;
    private String reason;
    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;
}
