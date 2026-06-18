package com.noticore.noticore_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SesEventDto {
    private String notificationType;
    private String eventType;
    private MailDto mail;
    private DeliveryDto delivery;
    private BounceDto bounce;
    private ComplaintDto complaint;
    private OpenDto open;
    private ClickDto click;
}
