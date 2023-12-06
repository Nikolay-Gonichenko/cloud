package ru.itmo.cloud.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException{
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    @Override
    public String getDescription() {
        return "Неверные логин и пароль";
    }
}
