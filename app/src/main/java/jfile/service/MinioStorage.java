package jfile.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.minio.*;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MinioStorage
{
    private static final Logger logger = LoggerFactory.getLogger(MinioStorage.class);
    private final MinioClient minioClient;
    private final String bucketName;
    private final int BUFFER_SIZE = 5 * 1024 * 1024;

    public MinioStorage(MinioClient minioClient, 
                        @Value("${minio.bucket}") String bucket)
    {
        this.minioClient = minioClient;
        this.bucketName = bucket;
        initializeBucket();
    }
    
    public CompletableFuture<String> uploadFile(InputStream inputStream, String contentType, String fileName)
    {
        String objectKey = UUID.randomUUID().toString() + "/" + fileName;
        return CompletableFuture.supplyAsync(() -> {
            try {
            PutObjectArgs putObj = new PutObjectArgs();
            minioClient.putObject(putObj.builder()
                                .bucket(bucketName)
                                .object(objectKey)
                                .stream(inputStream,-1, BUFFER_SIZE)
                                .contentType(contentType)
                                .build());
            return objectKey;
        }
        catch (Exception e)
        {
            logger.error("Upload failed: {}", e.getMessage());
            throw new RuntimeException("MinIO upload failed", e);
        }
    });
    }
    private void initializeBucket()
    {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                logger.info("Created new bucket: {}", bucketName);
            }
        }
        catch (Exception e) {
            logger.error("Error initializing bucket");
            throw new RuntimeException("Could not initialize MinIO bucket", e);
        }
    }

    
}



