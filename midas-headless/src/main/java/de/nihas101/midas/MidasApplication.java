package de.nihas101.midas;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = "de.nihas101.midas")
@EnableConfigurationProperties
public class MidasApplication {

    // TODO: Add an API, once the project is stable
    // TODO: Include this as artifact in the zip
    public static void main(String[] args) {
        SpringApplication.run(MidasApplication.class, args);
    }

}
