package ru.itmo.cloud.service.business;

import ru.itmo.cloud.model.input.UserDtoForLogin;

import java.util.UUID;

public interface UserService {

    UUID login(UserDtoForLogin userDtoForLogin);

    UUID register(UserDtoForLogin userDtoForLogin);
}
