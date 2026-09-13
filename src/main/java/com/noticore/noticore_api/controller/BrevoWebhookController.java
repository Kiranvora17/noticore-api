package com.noticore.noticore_api.controller;

import com.noticore.noticore_api.service.external.IBrevoWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class BrevoWebhookController {

    private final IBrevoWebhookService brevoWebhookService;

    @PostMapping("/brevo")
    public ResponseEntity<Void> handleBrevoEvent(
            @RequestBody String payload,
            @RequestHeader(value = "X-Brevo-Signature", required = false) String signature
    ) {
        log.info("Brevo webhook event received.");
        brevoWebhookService.handle(payload, signature);
        return ResponseEntity.ok().build();
    }
}
