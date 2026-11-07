package de.nihas101.midas.core.backup.service.snapshot;

import de.nihas101.midas.api.backup.MidasFile;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

class FilePropertiesLoaderTest {

    @Test
    void loadWhenFileDoesNotExist() throws IOException {
        final LocalMidasFileFactory localMidasFileFactory = Mockito.mock(LocalMidasFileFactory.class);
        final FilePropertiesLoader loader = new FilePropertiesLoader(localMidasFileFactory);
        final MidasFile file = Mockito.mock(MidasFile.class);
        Mockito.when(localMidasFileFactory.create(any())).thenReturn(file);
        Mockito.when(file.exists()).thenReturn(false);

        Properties result = loader.load();

        assertNull(result);
    }

    @Test
    void loadWhenFileExists() throws IOException {
        final LocalMidasFileFactory localMidasFileFactory = Mockito.mock(LocalMidasFileFactory.class);
        final FilePropertiesLoader loader = new FilePropertiesLoader(localMidasFileFactory);
        final MidasFile file = Mockito.mock(MidasFile.class);
        Mockito.when(localMidasFileFactory.create(any())).thenReturn(file);
        Mockito.when(file.exists()).thenReturn(true);
        String content = "test.key=test.value\nspring.datasource.url=jdbc:sqlite:test.db";
        Mockito.when(file.inputStream()).thenReturn(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

        Properties result = loader.load();

        assertNotNull(result);
        assertEquals("test.value", result.getProperty("test.key"));
        assertEquals("jdbc:sqlite:test.db", result.getProperty("spring.datasource.url"));
    }
}
