package ru.itmo.cloud.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
    private final HttpStatus httpStatus;

    public BaseException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public abstract String getDescription();

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
