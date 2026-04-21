package com.noticore.noticore_api.exception.ses;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;

public class DomainRegisterationException extends AppException {
    public DomainRegisterationException(String message) {
        super(message, 500, LocalDateTime.now());
    }
}
