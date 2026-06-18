package com.noticore.noticore_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class BounceDto {
    private String bounceType;
    private String bounceSubType;
    private List<BouncedRecipientDto> bouncedRecipients;
}
