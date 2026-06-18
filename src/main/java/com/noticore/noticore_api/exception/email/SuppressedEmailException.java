package com.noticore.noticore_api.exception.email;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;

public class SuppressedEmailException extends AppException {
    public SuppressedEmailException(String email) {
        super("Email is present in suppression list: " + email, 422, LocalDateTime.now());
    }
}
