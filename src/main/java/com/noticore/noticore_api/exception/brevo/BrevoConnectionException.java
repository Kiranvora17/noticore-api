package com.noticore.noticore_api.exception.brevo;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;

public class BrevoConnectionException extends AppException {

    public BrevoConnectionException(String message) {
        super(message, 503, LocalDateTime.now());
    }
}
