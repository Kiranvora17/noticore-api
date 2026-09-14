package com.noticore.noticore_api.exception.domain;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;

public class DomainInUseException extends AppException {
    public DomainInUseException(String domainName) {
        super("Domain has been used to send email and cannot be deleted: " + domainName, 409, LocalDateTime.now());
    }
}
