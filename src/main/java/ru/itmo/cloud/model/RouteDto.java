package ru.itmo.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto {
    private UUID id;
    private UUID userId;
    private List<Point> points;
    private List<Object> photos;
    private String description;
    private String bike;
    private Long minCost;
    private Long maxCost;
    private String dangers;
    private Boolean isActual;
}
