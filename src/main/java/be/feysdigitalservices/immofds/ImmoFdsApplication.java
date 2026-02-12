package be.feysdigitalservices.immofds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ImmoFdsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImmoFdsApplication.class, args);
    }
}
