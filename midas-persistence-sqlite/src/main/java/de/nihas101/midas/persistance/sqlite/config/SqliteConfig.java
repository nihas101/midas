package de.nihas101.midas.persistance.sqlite.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Data
@ConditionalOnProperty(
        name = "spring.datasource.driver-class-name",
        havingValue = "org.sqlite.JDBC"
)
public class SqliteConfig {
    private Optimize optimize = new Optimize();

}
