package com.noticore.noticore_api.exception.email;

import com.noticore.noticore_api.exception.base.AppException;

import java.time.LocalDateTime;

public class InvalidEmailException extends AppException {
    public InvalidEmailException(String email) {
        super("Invalid email: " + email, 400, LocalDateTime.now());
    }
}
