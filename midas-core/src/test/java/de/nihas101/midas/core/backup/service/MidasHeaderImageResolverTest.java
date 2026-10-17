package de.nihas101.midas.core.backup.service;

import de.nihas101.midas.core.export.pdf.config.PdfExportConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MidasHeaderImageResolverTest {

    @TempDir
    Path tempDir;

    @Test
    void resolveHeaderImageWhenNullConfig() {
        final MidasHeaderImageResolver resolver = new MidasHeaderImageResolver(null);
        assertTrue(resolver.resolveHeaderImage().isEmpty());
    }

    @Test
    void resolveHeaderImageWhenNullPath() {
        final PdfExportConfig config = mock(PdfExportConfig.class);
        when(config.getHeaderImagePath()).thenReturn(null);

        final MidasHeaderImageResolver resolver = new MidasHeaderImageResolver(config);
        assertTrue(resolver.resolveHeaderImage().isEmpty());
    }

    @Test
    void resolveHeaderImageWhenBlankPath() {
        final PdfExportConfig config = mock(PdfExportConfig.class);
        when(config.getHeaderImagePath()).thenReturn("   ");

        final MidasHeaderImageResolver resolver = new MidasHeaderImageResolver(config);
        assertTrue(resolver.resolveHeaderImage().isEmpty());
    }

    @Test
    void resolveHeaderImageWhenFileDoesNotExist() {
        final PdfExportConfig config = mock(PdfExportConfig.class);
        when(config.getHeaderImagePath()).thenReturn(tempDir.resolve("nonexistent.png").toString());

        final MidasHeaderImageResolver resolver = new MidasHeaderImageResolver(config);
        assertTrue(resolver.resolveHeaderImage().isEmpty());
    }

    @Test
    void resolveHeaderImageWhenFileExists() throws IOException {
        final Path imagePath = tempDir.resolve("test-logo.png");
        Files.writeString(imagePath, "fake-image-bytes");

        final PdfExportConfig config = mock(PdfExportConfig.class);
        when(config.getHeaderImagePath()).thenReturn(imagePath.toString());

        final MidasHeaderImageResolver resolver = new MidasHeaderImageResolver(config);
        final Optional<File> result = resolver.resolveHeaderImage();

        assertTrue(result.isPresent());
        assertEquals("test-logo.png", result.get().getName());
    }
}
