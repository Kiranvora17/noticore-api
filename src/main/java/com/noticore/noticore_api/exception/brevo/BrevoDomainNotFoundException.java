package com.noticore.noticore_api.exception.brevo;

import java.time.LocalDateTime;

public class BrevoDomainNotFoundException extends RuntimeException {

    private final LocalDateTime timestamp;

    public BrevoDomainNotFoundException(String domainName) {
        super("Domain not found on Brevo: " + domainName);
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
