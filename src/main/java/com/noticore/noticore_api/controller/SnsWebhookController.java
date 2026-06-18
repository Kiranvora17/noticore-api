package com.noticore.noticore_api.controller;

import com.noticore.noticore_api.service.external.ISnsWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class SnsWebhookController {

    private final ISnsWebhookService snsWebhookService;

    @PostMapping("/sns")
    public ResponseEntity<Void> handleSnsEvent(
            @RequestBody String payload,
            @RequestHeader(value = "X-Amz-Sns-Message-Type") String messageType
    ) {
        log.info("SNS event received. Type: {}", messageType);
        snsWebhookService.handle(messageType, payload);
        return ResponseEntity.ok().build();
    }

}