package ru.itmo.cloud.controller;

import com.clickhouse.client.internal.opencensus.metrics.export.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.itmo.cloud.model.input.InputPoints;
import ru.itmo.cloud.model.input.InputRouteDto;
import ru.itmo.cloud.service.business.RouteService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/cloud")
public class RouteController {

    private final RouteService routeService;

    @PostMapping("get-all")
    public ResponseEntity<?> getAll(@RequestBody InputPoints inputPoints) {
        return ResponseEntity.ok(routeService.getAll(inputPoints.getStartPoint(), inputPoints.getFinishPoint()));
    }

    @GetMapping("get-all-my")
    public ResponseEntity<?> getAllMy(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(routeService.getAllMy(userId));
    }

    @PostMapping(value = "add")
    public ResponseEntity<?> add(@RequestPart InputRouteDto inputRouteDto,
                                 @RequestParam("files") MultipartFile[] multipartFiles) {
        return ResponseEntity.ok(routeService.add(inputRouteDto, multipartFiles));
    }

    @PostMapping("update")
    public ResponseEntity<?> update(@RequestBody InputRouteDto inputRouteDto,
                                    @RequestParam("routeId") UUID routeId) {
        return ResponseEntity.ok(routeService.update(inputRouteDto, routeId));
    }

    @DeleteMapping("delete")
    public ResponseEntity<?> delete(@RequestParam("routeId") UUID routeId) {
        return ResponseEntity.ok(routeService.delete(routeId));
    }


}
