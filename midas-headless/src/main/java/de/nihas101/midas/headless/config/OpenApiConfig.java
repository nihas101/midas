package de.nihas101.midas.headless.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI midasOpenAPI() {
        final Info info = new Info()
                .title("Midas API")
                .description("REST API for Midas financial management application")
                .version("1.0.0");
        return new OpenAPI().info(info);
    }
}
