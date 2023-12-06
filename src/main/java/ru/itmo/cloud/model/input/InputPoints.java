package ru.itmo.cloud.model.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.cloud.model.Point;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputPoints {
    private Point startPoint;
    private Point finishPoint;
}
