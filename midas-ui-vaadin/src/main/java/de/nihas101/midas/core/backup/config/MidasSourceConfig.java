package de.nihas101.midas.core.backup.config;

import de.nihas101.midas.MidasApplication;
import de.nihas101.midas.api.backup.MidasSource;
import de.nihas101.midas.core.backup.DevelopmentMidasSource;
import de.nihas101.midas.core.backup.service.ProductionMidasSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URISyntaxException;

@Configuration
public class MidasSourceConfig {

    @Bean
    @Profile("dev")
    public MidasSource developmentMidasSource() {
        return new DevelopmentMidasSource();
    }

    @Bean
    @Profile("!dev")
    public MidasSource productionMidasSource() throws URISyntaxException {
        return new ProductionMidasSource(MidasApplication.class);
    }
}
