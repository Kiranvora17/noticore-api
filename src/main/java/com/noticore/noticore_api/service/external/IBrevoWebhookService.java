package com.noticore.noticore_api.service.external;

public interface IBrevoWebhookService {
    void handle(String payload, String authorizationHeader);
}
