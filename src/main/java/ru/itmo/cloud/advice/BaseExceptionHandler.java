package ru.itmo.cloud.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.itmo.cloud.exception.BaseException;

@ControllerAdvice
@RequiredArgsConstructor
public class BaseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BaseException.class})
    private ResponseEntity<Object> handleException(BaseException exception, WebRequest request) {

        return handleExceptionInternal(exception, exception.getDescription(),
                new HttpHeaders(), exception.getHttpStatus(), request);
    }
}

