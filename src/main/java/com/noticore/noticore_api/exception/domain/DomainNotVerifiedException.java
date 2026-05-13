package com.noticore.noticore_api.exception.domain;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;

public class DomainNotVerifiedException extends AppException {
    public DomainNotVerifiedException(String domainName) {
        super("Domain is not verified: " + domainName, 403, LocalDateTime.now());
    }
}
