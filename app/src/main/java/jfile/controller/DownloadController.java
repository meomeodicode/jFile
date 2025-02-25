package jfile.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jfile.dto.DownloadRequestDTO;
import jfile.dto.DownloadTaskDTO;
import jfile.service.DownloadService;

@RestController
@RequestMapping("/api/downloads")
public class DownloadController {

    private final DownloadService downloadService;

    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @PostMapping
    public ResponseEntity<DownloadTaskDTO> createDownload(
            @RequestHeader("Authorization") String token,
            @RequestBody DownloadRequestDTO request) {
        DownloadTaskDTO result = downloadService.createDownloadTask(token, request.getUrl());
        return ResponseEntity.created(URI.create("/api/downloads/" + result.getDownloadId())).body(result);
    }
}