package ru.itmo.cloud.model.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.cloud.model.Point;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutputRouteDto {
    private UUID id;
    private String ownerLogin;
    private List<Point> points;
    private List<byte[]> photos;
    private String description;
    private String bike;
    private Long minCost;
    private Long maxCost;
    private String dangers;
    private Boolean isActual;
}
