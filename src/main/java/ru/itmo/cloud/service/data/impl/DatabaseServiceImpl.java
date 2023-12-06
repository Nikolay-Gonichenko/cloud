package ru.itmo.cloud.service.data.impl;

import com.clickhouse.data.value.ClickHouseGeoPointValue;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.itmo.cloud.exception.SqlCustomException;
import ru.itmo.cloud.model.Point;
import ru.itmo.cloud.model.RouteDto;
import ru.itmo.cloud.model.UserDto;
import ru.itmo.cloud.model.input.InputRouteDto;
import ru.itmo.cloud.model.input.UserDtoForLogin;
import ru.itmo.cloud.service.data.DatabaseService;
import ru.itmo.cloud.util.PointMapperUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {

    @Value("${ch-props.host}")
    private String host;
    @Value("${ch-props.cert}")
    private String cert;
    @Value("${ch-props.user}")
    private String user;
    @Value("${ch-props.password}")
    private String password;

    private final PointMapperUtil pointMapperUtil;

    @Override
    public UserDto getUserByLogin(String login) {
        try (Connection connection = getConnection()) {
            var query = "select * from cloud.users where login = ?";
            var preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, login);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildUserFromResultSet(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SqlCustomException();
        }
    }

    @Override
    public UserDto getUserByLoginAndPassword(String login, String password) {
        try (Connection connection = getConnection()) {
            var query = "select * from cloud.users where login = ? and password = ?";
            var preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildUserFromResultSet(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SqlCustomException();
        }
    }

    private UserDto buildUserFromResultSet(ResultSet resultSet) throws SQLException {
        return UserDto
                .builder()
                .id(UUID.fromString(resultSet.getString("id")))
                .login(resultSet.getString("login"))
                .password(resultSet.getString("password"))
                .build();
    }

    @Override
    public UUID insertUser(UserDtoForLogin userDtoForLogin) {
        try (Connection connection = getConnection()) {
            var query = "insert into cloud.users(id, login, password) values (generateUUIDv4(), ?, ?)";
            var preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userDtoForLogin.getLogin());
            preparedStatement.setString(2, userDtoForLogin.getPassword());
            preparedStatement.executeUpdate();
            return getUserByLogin(userDtoForLogin.getLogin()).getId();
        } catch (SQLException throwables) {
            throw new SqlCustomException();
        }
    }

    @Override
    public List<RouteDto> getAll() {
        var routes = new ArrayList<RouteDto>();
        try (Connection connection = getConnection()) {
            var query = "select * from cloud.routes";
            var preparedStatement = connection.prepareStatement(query);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                routes.add(buildRouteDtoFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SqlCustomException();
        }
        return routes;
    }

    private RouteDto buildRouteDtoFromResultSet(ResultSet resultSet) throws SQLException {
        var points = pointMapperUtil
                .mapResultSetToPoint((Object[]) resultSet.getArray("points").getArray());
        return RouteDto
                .builder()
                .id(UUID.fromString(resultSet.getString("id")))
                .userId(UUID.fromString(resultSet.getString("userId")))
                .points(points)
                .description(resultSet.getString("description"))
                .bike(resultSet.getString("bike"))
                .minCost(resultSet.getLong("minCost"))
                .maxCost(resultSet.getLong("maxCost"))
                .dangers(resultSet.getString("dangers"))
                .isActual(resultSet.getBoolean("isActual"))
                .build();
    }

    @Override
    @Cacheable(value = "popularRow")
    public List<RouteDto> getAllWithPointStartAndPointFinish(Point pointStart, Point pointFinish) {
        var routes = new ArrayList<RouteDto>();
        try (Connection connection = getConnection()) {
            var query = "select * from cloud.routes where points[1] = (?, ?) and points[length(points)] = (?, ?)";
            var preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, pointStart.getLat());
            preparedStatement.setDouble(2, pointStart.getLon());
            preparedStatement.setDouble(3, pointFinish.getLat());
            preparedStatement.setDouble(4, pointFinish.getLon());
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                routes.add(buildRouteDtoFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SqlCustomException();
        }
        return routes;
    }

    @Override
    public UUID insertRoute(InputRouteDto routeDto) {
        var pointValues = mapToClickHousePoint(routeDto.getPoints());
        var id = UUID.randomUUID();
        try (Connection connection = getConnection()) {
            var query = "insert into cloud.routes(id, userId, points, description, bike, minCost, maxCost, dangers, isActual) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            var preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, routeDto.getUserId());
            preparedStatement.setArray(3, connection.createArrayOf("Point", pointValues));
            preparedStatement.setString(4, routeDto.getDescription());
            preparedStatement.setString(5, routeDto.getBike());
            preparedStatement.setLong(6, routeDto.getMinCost());
            preparedStatement.setLong(7, routeDto.getMaxCost());
            preparedStatement.setString(8, routeDto.getDangers());
            preparedStatement.setBoolean(9, routeDto.getIsActual());
            preparedStatement.executeUpdate();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SqlCustomException();
        }
    }

    @Override
    public Boolean updateRoute(InputRouteDto inputRouteDto, UUID routeId) {
        var pointValues = mapToClickHousePoint(inputRouteDto.getPoints());
        try (Connection connection = getConnection()) {
            var query = "alter table cloud.routes update points = ?, description = ?, bike = ?, minCost = ?, maxCost = ?, dangers = ?, isActual = ? where id = ?";
            var preparedStatement = connection.prepareStatement(query);
            preparedStatement.setArray(1, connection.createArrayOf("Point", pointValues));
            preparedStatement.setString(2, inputRouteDto.getDescription());
            preparedStatement.setString(3, inputRouteDto.getBike());
            preparedStatement.setLong(4, inputRouteDto.getMinCost());
            preparedStatement.setLong(5, inputRouteDto.getMaxCost());
            preparedStatement.setString(6, inputRouteDto.getDangers());
            preparedStatement.setBoolean(7, inputRouteDto.getIsActual());
            preparedStatement.setObject(8, routeId);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SqlCustomException();
        }
    }

    private ClickHouseGeoPointValue[] mapToClickHousePoint(List<Point> points) {
        var clickHouseGeoPointValues = new ClickHouseGeoPointValue[points.size()];
        for (int i = 0; i < points.size(); i++) {
            var point = points.get(i);
            clickHouseGeoPointValues[i] = ClickHouseGeoPointValue.of(new double[]{point.getLat(), point.getLon()});
        }
        return clickHouseGeoPointValues;
    }

    @Override
    public Boolean deleteRoute(UUID routeId) {
        try (Connection connection = getConnection()) {
            var query = "alter table cloud.routes delete where id = ?";
            var preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, routeId);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SqlCustomException();
        }
    }

    @Override
    public UserDto getUserById(UUID userId) {
        try (Connection connection = getConnection()) {
            var query = "select * from cloud.users where id = ?";
            var preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, userId);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return buildUserFromResultSet(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SqlCustomException();
        }
    }


    private Connection getConnection() throws SQLException {
        String url = String.format("jdbc:clickhouse://%s:8443/%s?ssl=1&sslmode=strict&sslrootcert=%s", host, "cloud", cert);
        return DriverManager.getConnection(url, user, password);
    }
}
