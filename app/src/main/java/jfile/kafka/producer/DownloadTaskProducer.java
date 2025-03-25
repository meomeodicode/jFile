package jfile.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import jfile.model.DownloadTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DownloadTaskProducer {
    private static final Logger logger = LoggerFactory.getLogger(DownloadTaskProducer.class);
    @Value("${spring.kafka.topics.downloads}")
    private String TOPIC;
    private final KafkaTemplate<String, DownloadTask> kafkaTemplate;

    public DownloadTaskProducer(KafkaTemplate<String, DownloadTask> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDownloadTask(DownloadTask task) {
        logger.info("Sending download task: {}", task.getDownloadId());
        kafkaTemplate.send(TOPIC, task.getDownloadId(), task)
            .thenAccept(result -> logger.info("Task sent successfully"))
            .exceptionally(ex -> {
                logger.error("Failed to send task", ex);
                return null;
            });
    }
}