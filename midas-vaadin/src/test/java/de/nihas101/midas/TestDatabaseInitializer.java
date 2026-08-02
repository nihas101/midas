package de.nihas101.midas;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Slf4j
public class TestDatabaseInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final Environment env = applicationContext.getEnvironment();
        if (!Arrays.asList(env.getActiveProfiles()).contains("test")) {
            return;
        }
        final String dbUrl = env.getProperty("spring.datasource.url");
        if (dbUrl == null || !dbUrl.startsWith("jdbc:sqlite:")) {
            return;
        }
        final String dbPath = dbUrl.substring("jdbc:sqlite:".length());
        // Handle optional file: prefix or memory markers if necessary,
        // but for "midas-test.db" it's straightforward.
        if (dbPath.contains(":memory:") || dbPath.isEmpty()) {
            return;
        }

        log.info("Cleaning up database from last test run...");
        try {
            Files.deleteIfExists(Paths.get(dbPath));
        } catch (IOException e) {
            log.error("TestDatabaseInitializer: Failed to delete test database file {}: {}", dbPath, e.getMessage());
        }
    }
}
