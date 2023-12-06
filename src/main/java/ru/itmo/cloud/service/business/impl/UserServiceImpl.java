package ru.itmo.cloud.service.business.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.cloud.exception.UserAlreadyExistException;
import ru.itmo.cloud.exception.UserNotFoundException;
import ru.itmo.cloud.model.input.UserDtoForLogin;
import ru.itmo.cloud.service.business.UserService;
import ru.itmo.cloud.service.data.DatabaseService;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final DatabaseService databaseService;

    @Override
    public UUID login(UserDtoForLogin userDtoForLogin) {
        var user = databaseService
                .getUserByLoginAndPassword(userDtoForLogin.getLogin(), userDtoForLogin.getPassword());
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user.getId();
    }

    @Override
    public UUID register(UserDtoForLogin userDtoForLogin) {
        var user = databaseService.getUserByLogin(userDtoForLogin.getLogin());
        if (user != null) {
            throw new UserAlreadyExistException();
        }
        return databaseService.insertUser(userDtoForLogin);
    }
}
