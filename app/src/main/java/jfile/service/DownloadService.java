package jfile.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.jsonwebtoken.Claims;
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
        Claims claims = jwtTokenProvider.extractClaims(token.replace("Bearer ", ""));
        Long userId = Long.parseLong(claims.getSubject());
        String username = claims.get("username", String.class);

        DownloadTask task = new DownloadTask();
        task.setDownloadId(UUID.randomUUID().toString());
        task.setUser(new User(userId, username)); 
        task.setUrl(url);
        task.setDownloadStatus((short) 0);
        task.setMetadata(destinationPath);

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
}