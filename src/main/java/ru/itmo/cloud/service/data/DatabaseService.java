package ru.itmo.cloud.service.data;

import ru.itmo.cloud.model.Point;
import ru.itmo.cloud.model.RouteDto;
import ru.itmo.cloud.model.UserDto;
import ru.itmo.cloud.model.input.InputRouteDto;
import ru.itmo.cloud.model.input.UserDtoForLogin;

import java.util.List;
import java.util.UUID;

public interface DatabaseService {
    UserDto getUserByLogin(String login);

    UserDto getUserByLoginAndPassword(String login, String password);

    UUID insertUser(UserDtoForLogin userDtoForLogin);

    List<RouteDto> getAll();

    List<RouteDto> getAllWithPointStartAndPointFinish(Point pointStart, Point pointFinish);

    UUID insertRoute(InputRouteDto routeDto);

    Boolean updateRoute(InputRouteDto inputRouteDto, UUID routeId);

    Boolean deleteRoute(UUID routeId);

    UserDto getUserById(UUID userId);
}
