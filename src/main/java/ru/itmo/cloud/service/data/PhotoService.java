package ru.itmo.cloud.service.data;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PhotoService {
    List<byte[]> getPhotosByRouteId(UUID routeId);

    void savePhotos(MultipartFile[] files, UUID routeId);

    void deletePhotos(UUID routeId);
}
