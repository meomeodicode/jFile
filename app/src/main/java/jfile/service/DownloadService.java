package jfile.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jfile.dao.DownloadRepository;
import jfile.dto.DownloadTaskDTO;
import jfile.kafka.producer.DownloadTaskProducer;
import jfile.model.DownloadTask;
import jfile.model.User;
import jfile.utility.JWTTokenProvider;

@Service
public class DownloadService {
    private final JWTTokenProvider jwtTokenProvider;
    private final DownloadRepository downloadRepository;
    private final DownloadTaskProducer producer;
    
    @Value("${download.destination}")
    private String destinationPath;

    public DownloadService(JWTTokenProvider jwtTokenProvider, DownloadRepository downloadRepository, DownloadTaskProducer producer) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.downloadRepository = downloadRepository;
        this.producer = producer;
    }

    @Transactional
    public DownloadTaskDTO createDownloadTask(String token, String url) {
        if (!jwtTokenProvider.validateToken(token.replace("Bearer ", ""))) {
            throw new JwtException("Invalid token");
        }
        
        Claims claims = jwtTokenProvider.extractClaims(token.replace("Bearer ", ""));
        Long userId = Long.parseLong(claims.getSubject());
        String username = claims.get("username", String.class);
        String filename = extractFilenameFromUrl(url);
        DownloadTask task = new DownloadTask();
        task.setDownloadId(UUID.randomUUID().toString());
        task.setUser(new User(userId, username));
        task.setUrl(url);
        task.setDownloadStatus((short) 0);  // Initial status
        task.setDownloadType((short) 1);    // Default download type
        task.setMetadata(filename);
        
        DownloadTask savedTask = downloadRepository.save(task);
        producer.sendDownloadTask(savedTask);
        return convertToDTO(savedTask);
    }

    private DownloadTaskDTO convertToDTO(DownloadTask task) {
        return new DownloadTaskDTO(
            task.getDownloadId(),
            task.getUser().getUid(),
            task.getDownloadType(),
            task.getUrl(),
            task.getDownloadStatus(),
            task.getMetadata()
        );
    }

    private String extractFilenameFromUrl(String url) {
        try {
            // Remove query parameters if present
            String urlWithoutQuery = url.contains("?") 
                ? url.substring(0, url.indexOf("?")) 
                : url;
                
            // Get the path part of the URL (after last forward slash)
            String path = urlWithoutQuery.substring(urlWithoutQuery.lastIndexOf('/') + 1);
            
            // If path is empty or has no extension, generate a filename
            if (path.isEmpty() || !path.contains(".")) {
                return "download_" + System.currentTimeMillis();
            }
            
            // URL decode the filename (spaces and special characters)
            path = java.net.URLDecoder.decode(path, "UTF-8");
            
            // Remove problematic characters from filename
            path = path.replaceAll("[\\\\/:*?\"<>|]", "_");
            
            return path;
        } catch (Exception e) {
            // If any errors occur, use timestamp as fallback
            return "download_" + System.currentTimeMillis();
        }
    }
}