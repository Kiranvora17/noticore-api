package com.noticore.noticore_api.exception.base;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AppException extends RuntimeException {

    private final int status;
    private final LocalDateTime timestamp;

    public AppException(String message, int status, LocalDateTime timestamp) {

        super(message);
        this.status = status;
        this.timestamp = timestamp;
    }
}
