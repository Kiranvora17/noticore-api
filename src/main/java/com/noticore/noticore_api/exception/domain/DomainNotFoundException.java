package com.noticore.noticore_api.exception.domain;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;
import java.util.UUID;

public class DomainNotFoundException extends AppException {
    public DomainNotFoundException(UUID domainId) {
        super("Domain not found with id: " + domainId, 404, LocalDateTime.now());
    }

    public DomainNotFoundException(String domainName) {
        super("Domain not found with name: " + domainName, 404, LocalDateTime.now());
    }
}
