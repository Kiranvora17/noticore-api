package com.noticore.noticore_api.exception.domain;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;

public class DomainExistException extends AppException {
    public DomainExistException(String domainName) {
        super("Domain already exists: " + domainName, 409, LocalDateTime.now());
    }
}
