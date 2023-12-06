package ru.itmo.cloud.service.business;

import org.springframework.web.multipart.MultipartFile;
import ru.itmo.cloud.model.Point;
import ru.itmo.cloud.model.input.InputRouteDto;
import ru.itmo.cloud.model.output.OutputRouteDto;

import java.util.List;
import java.util.UUID;

public interface RouteService {

    List<OutputRouteDto> getAll(Point pointStart, Point pointFinish);

    List<OutputRouteDto> getAllMy(UUID userId);

    boolean add(InputRouteDto inputRouteDto, MultipartFile[] multipartFiles);

    boolean update(InputRouteDto inputRouteDto, UUID routeId);

    boolean delete(UUID routeId);
}
