package jfile.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@Configuration
public class EnvConfig {
    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);
    
    @Autowired
    private ConfigurableEnvironment environment;
    
    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();
    }
    
    @PostConstruct
    public void logEnvironmentInfo() {
        logger.info("🔍 Environment configuration initialized");
        logger.debug("DB_HOST: {}", environment.getProperty("DB_HOST", "localhost"));
        logger.debug("DB_PORT: {}", environment.getProperty("DB_PORT", "5432"));
        logger.debug("DB_NAME: {}", environment.getProperty("DB_NAME", "JFile"));
        logger.debug("DB_USERNAME: {}", environment.getProperty("DB_USERNAME", "postgres"));
        logger.debug("POSTGRESQL_PASS: ****");
        logger.debug("DOWNLOAD_PATH: {}", environment.getProperty("DOWNLOAD_PATH", "D:/"));
    }
}