package com.noticore.noticore_api.service.external;

public interface ISnsWebhookService {
    void handle(String messageType, String payload);
}
