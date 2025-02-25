package jfile.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Logger logger = LoggerFactory.getLogger(DotenvInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        // Load dotenv
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();
        
        // Create property source with highest precedence
        Map<String, Object> propertySource = new HashMap<>();
        
        dotenv.entries().forEach(entry -> {
            // Add to both system properties and our property source map
            System.setProperty(entry.getKey(), entry.getValue());
            propertySource.put(entry.getKey(), entry.getValue());
            logger.debug("Loaded environment variable: {}", entry.getKey());
        });
        
        // Add our property source with highest precedence
        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", propertySource));
        
        logger.info("Environment variables loaded from .env file");
    }
}