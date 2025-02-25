package jfile;

import jfile.config.DotenvInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(App.class);
        application.addInitializers(new DotenvInitializer());
        application.run(args);
    }
}