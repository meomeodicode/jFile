package jfile.logic;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.stereotype.Service;

@Service
public class FileDownloader {

    private final HttpClient httpClient;

    public FileDownloader() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public CompletableFuture<Void> downloadFile(String url, String destination) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            .GET()
            .build();

        return httpClient
            .sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
            .thenApply(response -> {
                if (response.statusCode() == 200) {
                    try (InputStream in = response.body();
                         FileOutputStream fos = new FileOutputStream(destination)) {

                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        long totalBytes = 0;
                        long contentSize = response.headers()
                            .firstValueAsLong("Content-Length").orElse(-1);

                        while ((bytesRead = in.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                            totalBytes += bytesRead;
                            if (contentSize > 0) {
                                int progress = (int) ((totalBytes * 100) / contentSize);
                                System.out.println("Download Progress: " + progress + "%");
                            }
                        }
                        System.out.println("Download complete: " + destination);
                    } catch (IOException e) {
                        throw new RuntimeException("Error writing file", e);
                    }
                } else {
                    System.out.println("Failed to download: HTTP " + response.statusCode());
                }
                return null;
            });
    }
}