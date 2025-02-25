package jfile.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import jfile.model.DownloadTask;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    
    @Bean
    public ConsumerFactory<String, DownloadTask> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "download-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "jfile.model");
        
        return new DefaultKafkaConsumerFactory<>(props, 
            new StringDeserializer(),
            new JsonDeserializer<>(DownloadTask.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DownloadTask> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DownloadTask> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}