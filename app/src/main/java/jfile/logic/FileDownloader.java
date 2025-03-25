package jfile.logic;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import jfile.App;
import jfile.controller.AuthController;
import jfile.service.MinioStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileDownloader {
    private static final Logger logger = LoggerFactory.getLogger(FileDownloader.class);
    private final HttpClient httpClient;
    private final MinioStorage minioStorage;

    public FileDownloader(MinioStorage minioStorageService) {
        this.httpClient = HttpClient.newHttpClient();
        this.minioStorage = minioStorageService;
        logger.info("HttpClient initialized");
    }

    public CompletableFuture<String> downloadFile(String url, String filename) {
        logger.info("Starting download from {} to {}", url, filename);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .GET()
            .build();
    
        return httpClient
            .sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
            .thenCompose(response -> {
                if (response.statusCode() == 200) {
                    try {
                        String contentType = response.headers().
                                            firstValue("Content-Type").
                                            orElse("application/octet-stream");
                        Long contentSize = response.headers().
                                            firstValueAsLong("Content-Length").
                                            orElse(-1);
                        logger.info("Content-Type: {}, Size: {} bytes", contentType, contentSize);
                    
                        return minioStorage.uploadFile(
                            response.body(),
                            contentType,
                            filename
                        ).whenComplete((objectKey, ex) -> {
                            if (ex != null) {
                                logger.error("Upload failed: {}", ex.getMessage());
                            } else {
                                logger.info("Upload complete: {}", objectKey);
                            }
                        });
                    }
                    catch (RuntimeException e) {
                        logger.error("Error writing file: {}", e.getMessage(), e);
                        throw new RuntimeException("Error writing file", e);
                    }
                } else {
                    String errorMsg = "Failed to download: HTTP " + response.statusCode();
                    logger.error(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            });
    }
}