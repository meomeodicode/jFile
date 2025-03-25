package jfile.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import jfile.dto.DownloadRequestDTO;
import jfile.dto.DownloadTaskDTO;
import jfile.dto.ErrorResponseDTO;
import jfile.service.DownloadService;

@RestController
@RequestMapping("/api/downloads")
public class DownloadController {

    private final DownloadService downloadService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @PostMapping
    public ResponseEntity<?> createDownload(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody DownloadRequestDTO request) {
        try {
            logger.debug("Received token: {}", token);
            if (!token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("Invalid token format", "Token must start with 'Bearer '"));
            }
            
            DownloadTaskDTO result = downloadService.createDownloadTask(token, request.getUrl());
            return ResponseEntity.created(URI.create("/api/downloads/" + result.getDownloadId())).body(result);

        } catch (JwtException e) {
            logger.error("JWT validation failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO("Authentication failed", e.getMessage()));
        }
    }
}