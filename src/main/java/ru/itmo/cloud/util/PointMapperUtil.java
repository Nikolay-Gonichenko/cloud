package ru.itmo.cloud.util;

import org.springframework.stereotype.Component;
import ru.itmo.cloud.model.Point;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PointMapperUtil {

    public List<Point> mapResultSetToPoint(Object[] points) {
        return Arrays.stream(points)
                .map(point -> (double[]) point)
                .map(point -> Point.builder().lat(point[0]).lon(point[1]).build())
                .collect(Collectors.toList());
    }
}
