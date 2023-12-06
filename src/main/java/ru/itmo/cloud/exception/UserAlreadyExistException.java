package ru.itmo.cloud.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistException extends BaseException{
    public UserAlreadyExistException() {
        super(HttpStatus.BAD_REQUEST);
    }

    @Override
    public String getDescription() {
        return "Пользователь с таким логином уже есть";
    }
}
