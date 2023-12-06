package ru.itmo.cloud.exception;

import org.springframework.http.HttpStatus;

public class ObjectStorageException extends BaseException{
    public ObjectStorageException() {
        super(HttpStatus.BAD_REQUEST);
    }

    @Override
    public String getDescription() {
        return "Ошибка вызвана образением к Object Storage";
    }
}
