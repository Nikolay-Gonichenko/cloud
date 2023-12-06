package ru.itmo.cloud.model.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.cloud.model.Point;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputRouteDto {
    private UUID userId;
    private List<Point> points;
    private String description;
    private String bike;
    private Long minCost;
    private Long maxCost;
    private String dangers;
    private Boolean isActual;
}
