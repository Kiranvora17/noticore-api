package com.noticore.noticore_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainRequestDto {

    @NotBlank(message = "Domain name is required")
    private String domainName;
}
