package de.nihas101.midas.backup.config;

import de.nihas101.midas.backup.service.DevelopmentMidasSource;
import de.nihas101.midas.backup.service.MidasSource;
import de.nihas101.midas.backup.service.ProductionMidasSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URISyntaxException;

@Configuration
public class MidasSourceConfig {

    @Bean
    @Profile("!dev")
    public MidasSource productionMidasSource() throws URISyntaxException {
        return new ProductionMidasSource();
    }

    @Bean
    @Profile("dev")
    public MidasSource developmentMidasSource() {
        return new DevelopmentMidasSource();
    }
}
