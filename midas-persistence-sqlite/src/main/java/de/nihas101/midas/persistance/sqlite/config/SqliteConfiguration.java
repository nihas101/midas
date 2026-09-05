package de.nihas101.midas.persistance.sqlite.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "spring.datasource.driver-class-name",
        havingValue = "org.sqlite.JDBC"
)
public class SqliteConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "midas.db.sqlite")
    public SqliteConfig sqliteConfig() {
        return new SqliteConfig();
    }
}
