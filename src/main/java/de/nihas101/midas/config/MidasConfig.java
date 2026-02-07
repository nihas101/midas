package de.nihas101.midas.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "midas")
public class MidasConfig {
    ThemeConfig theme = new ThemeConfig();
}
