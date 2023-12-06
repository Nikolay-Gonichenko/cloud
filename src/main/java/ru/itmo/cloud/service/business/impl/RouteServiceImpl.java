package ru.itmo.cloud.service.business.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.itmo.cloud.model.Point;
import ru.itmo.cloud.model.RouteDto;
import ru.itmo.cloud.model.UserDto;
import ru.itmo.cloud.model.input.InputRouteDto;
import ru.itmo.cloud.model.output.OutputRouteDto;
import ru.itmo.cloud.service.business.RouteService;
import ru.itmo.cloud.service.data.DatabaseService;
import ru.itmo.cloud.service.data.PhotoService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final DatabaseService databaseService;
    private final PhotoService photoService;

    @Override
    public List<OutputRouteDto> getAll(Point pointStart, Point pointFinish) {
        if (pointStart == null && pointFinish == null)
            return databaseService.getAll().stream()
                    .map(this::mapRoute)
                    .collect(Collectors.toList());
        return databaseService.getAllWithPointStartAndPointFinish(pointStart, pointFinish).stream()
                .map(this::mapRoute)
                .collect(Collectors.toList());
    }

    private OutputRouteDto mapRoute(RouteDto route) {
         return OutputRouteDto.builder()
                 .id(route.getId())
                 .ownerLogin(databaseService.getUserById(route.getUserId()).getLogin())
                 .points(route.getPoints())
                 .photos(photoService.getPhotosByRouteId(route.getId()))
                 .description(route.getDescription())
                 .bike(route.getBike())
                 .minCost(route.getMinCost())
                 .maxCost(route.getMaxCost())
                 .dangers(route.getDangers())
                 .isActual(route.getIsActual())
                 .build();
    }

    @Override
    public List<OutputRouteDto> getAllMy(UUID userId) {
        var userDto = databaseService.getUserById(userId);
        return getAll(null, null).stream()
                .filter(route -> route.getOwnerLogin().equals(userDto.getLogin()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean add(InputRouteDto inputRouteDto, MultipartFile[] multipartFiles) {
        var routeId = databaseService.insertRoute(inputRouteDto);
        photoService.savePhotos(multipartFiles, routeId);
        return true;
    }

    @Override
    public boolean update(InputRouteDto inputRouteDto, UUID routeId) {
        return databaseService.updateRoute(inputRouteDto, routeId);
    }

    @Override
    public boolean delete(UUID routeId) {
        photoService.deletePhotos(routeId);
        return databaseService.deleteRoute(routeId);
    }
}
