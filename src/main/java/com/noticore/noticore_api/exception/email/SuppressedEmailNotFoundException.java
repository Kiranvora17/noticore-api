package com.noticore.noticore_api.exception.email;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;

public class SuppressedEmailNotFoundException extends AppException {
    public SuppressedEmailNotFoundException(String email) {
        super("Email not found in suppression list: " + email, 404, LocalDateTime.now());
    }
}
