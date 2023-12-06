package ru.itmo.cloud.service.data.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.itmo.cloud.exception.ObjectStorageException;
import ru.itmo.cloud.service.data.PhotoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoServiceImpl implements PhotoService {
    private final String accessKey = "YCAJE_7FoCUzQhLeaYuj6GqN6";
    private final String secretKey = "YCM8wWCnp6ZvbY6F7hLthlTGyS5-_bgVHWwexcet";
    private final String bucketName = "itmo-cloud-nikolay-gonichenko";

    @Override
    public List<byte[]> getPhotosByRouteId(UUID routeId) {
        var s3 = getS3();
        var folderName = routeId.toString();
        var listObjectsRequest = new ListObjectsV2Request();
        listObjectsRequest.setBucketName(bucketName);
        listObjectsRequest.setPrefix(folderName + "/");
        var objectListing = s3.listObjectsV2(listObjectsRequest);
        List<byte[]> files = new ArrayList<>();
        for (S3ObjectSummary s3ObjectSummary: objectListing.getObjectSummaries()) {
            try {
                var s3Object = s3.getObject(new GetObjectRequest(bucketName, s3ObjectSummary.getKey()));
                files.add(IOUtils.toByteArray(s3Object.getObjectContent()));
            } catch (IOException | AmazonS3Exception e) {
                e.printStackTrace();
                throw new ObjectStorageException();
            }
        }
        return files;
    }

    @Override
    public void savePhotos(MultipartFile[] files, UUID routeId) {
        var s3 = getS3();
        for (MultipartFile file : files) {
            try {
                var metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                metadata.setContentType(file.getContentType());
                var request = new PutObjectRequest(bucketName,
                        routeId + "/" + file.getOriginalFilename(),
                        file.getInputStream(), metadata);
                s3.putObject(request);
            } catch (IOException | AmazonS3Exception e) {
                e.printStackTrace();
                throw new ObjectStorageException();
            }
        }
    }

    @Override
    public void deletePhotos(UUID routeId) {
        var s3 = getS3();
        var folderName = routeId.toString();
        var listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(bucketName);
        listObjectsRequest.setPrefix(folderName + "/");
        var objectListing = s3.listObjects(listObjectsRequest);
        for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
            s3.deleteObject(bucketName, os.getKey());
        }
        s3.deleteObject(bucketName, folderName);
    }

    private AmazonS3 getS3() {
        var credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net","ru-central1"
                        )
                )
                .build();
    }
}
