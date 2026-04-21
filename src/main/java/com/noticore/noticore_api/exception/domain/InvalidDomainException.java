package com.noticore.noticore_api.exception.domain;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;

public class InvalidDomainException extends AppException {
    public InvalidDomainException(String domainName) {
        super("Invalid domain: " + domainName, 400, LocalDateTime.now());
    }
}
