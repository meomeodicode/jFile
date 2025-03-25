package jfile.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import jfile.model.DownloadTask;
import jfile.dao.DownloadRepository;
import jfile.logic.FileDownloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DownloadTaskConsumer {
    private static final Logger logger = LoggerFactory.getLogger(DownloadTaskConsumer.class);
    private final DownloadRepository downloadRepository;
    private final FileDownloader fileDownloader;

    public DownloadTaskConsumer(DownloadRepository downloadRepository, FileDownloader fileDownloader) {
        logger.info("🔄 Initializing DownloadTaskConsumer");
        this.downloadRepository = downloadRepository;
        this.fileDownloader = fileDownloader;
    }

    @KafkaListener(
        topics = "${spring.kafka.topics.downloads}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void processDownload(DownloadTask task) {
        logger.info("Processing download task: {}", task.getDownloadId());
        try {
            task.setDownloadStatus((short) 1); // In progress
            downloadRepository.save(task);
            
            fileDownloader.downloadFile(task.getUrl(), task.getMetadata())
                .thenAccept(v -> {
                    task.setDownloadStatus((short) 2); // Completed
                    downloadRepository.save(task);
                })
                .exceptionally(ex -> {
                    logger.error("Download failed", ex);
                    task.setDownloadStatus((short) -1); // Failed
                    downloadRepository.save(task);
                    return null;
                });
        } catch (Exception e) {
            logger.error("Download processing failed", e);
            task.setDownloadStatus((short) -1);
            downloadRepository.save(task);
        }
    }
}