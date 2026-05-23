package de.nihas101.midas.backup.service.snapshot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;

class FilePropertiesLoaderTest {

    private final FilePropertiesLoader loader = new FilePropertiesLoader();

    @Test
    void loadWhenFileDoesNotExist() throws IOException {
        try (MockedStatic<Files> files = Mockito.mockStatic(Files.class)) {
            files.when(() -> Files.exists(any())).thenReturn(false);

            Properties result = loader.load();

            Assertions.assertNull(result);
        }
    }

    @Test
    void loadWhenFileExists() throws IOException {
        String content = "test.key=test.value\nspring.datasource.url=jdbc:sqlite:test.db";
        // TODO: Introduce a new class for this in the FilePropertiesLoader to avoid having to do this and increase isolation
        try (MockedStatic<Files> files = Mockito.mockStatic(Files.class)) {
            files.when(() -> Files.exists(any())).thenReturn(true);
            files.when(() -> Files.newInputStream(any()))
                    .thenReturn(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

            Properties result = loader.load();

            Assertions.assertNotNull(result);
            Assertions.assertEquals("test.value", result.getProperty("test.key"));
            Assertions.assertEquals("jdbc:sqlite:test.db", result.getProperty("spring.datasource.url"));
        }
    }
}
