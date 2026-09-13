package com.noticore.noticore_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrevoEventDto {

    private String event;
    private String email;
    private Long id;
    private String date;
    private Long ts;

    @JsonProperty("message-id")
    private String messageId;

    private String subject;
    private String tag;

    @JsonProperty("sending_ip")
    private String sendingIp;

    @JsonProperty("ts_event")
    private Long tsEvent;

    private String reason;

    private String ip;

    @JsonProperty("user_agent")
    private String userAgent;

    private String link;
}
