package com.noticore.noticore_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuppressedEmailRequestDto {

    @NotBlank(message = "Email is required")
    private String email;

    private String reason;
}
