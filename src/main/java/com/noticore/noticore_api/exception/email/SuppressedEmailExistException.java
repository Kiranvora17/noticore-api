package com.noticore.noticore_api.exception.email;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;

public class SuppressedEmailExistException extends AppException {
    public SuppressedEmailExistException(String email) {
        super("Email is already present in suppression list: " + email, 409, LocalDateTime.now());
    }
}
