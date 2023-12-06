package ru.itmo.cloud.exception;

import org.springframework.http.HttpStatus;

public class SqlCustomException extends BaseException {


    public SqlCustomException() {
        super(HttpStatus.NOT_FOUND);
    }

    @Override
    public String getDescription() {
        return "Неизвестная ошибка SQL!";
    }
}
