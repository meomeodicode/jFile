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

    public DownloadTaskConsumer(
            DownloadRepository downloadRepository,
            FileDownloader fileDownloader) {
        this.downloadRepository = downloadRepository;
        this.fileDownloader = fileDownloader;
    }

    @KafkaListener(topics = "${spring.kafka.topics.downloads}",
                   groupId = "${spring.kafka.consumer.group-id}")
    public void processDownload(DownloadTask task) {
        logger.info("Processing download task: {}", task.getDownloadId());
        try {
            downloadRepository.save(task);
            fileDownloader.downloadFile(task.getUrl(), task.getMetadata());
            
        } catch (Exception e) {
            logger.error("Download failed", e);
            downloadRepository.save(task);
        }
    }
}