package com.noticore.noticore_api.exception.ses;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;

public class AWSConnectionException extends AppException {

    public AWSConnectionException(String message) {
        super(message, 503, LocalDateTime.now());
    }
}
