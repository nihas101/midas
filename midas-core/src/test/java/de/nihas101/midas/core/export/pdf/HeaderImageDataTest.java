package de.nihas101.midas.core.export.pdf;

import de.nihas101.midas.core.backup.service.MidasHeaderImageResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeaderImageDataTest {

    @Mock
    private MidasHeaderImageResolver headerImageResolver;

    @InjectMocks
    private HeaderImageData headerImageData;

    @Test
    void dataUri_returnsNull_whenImagePathIsInvalid() {
        when(headerImageResolver.resolveHeaderImage()).thenReturn(Optional.empty());

        assertNull(headerImageData.dataUri());
    }

    @Test
    void dataUri_returnsPngDataUri_whenPngFileExists(@TempDir Path tempDir) throws IOException {
        final byte[] imageBytes = new byte[]{(byte) 0x89, 'P', 'N', 'G'};
        final Path imageFile = tempDir.resolve("logo.png");
        Files.write(imageFile, imageBytes);
        when(headerImageResolver.resolveHeaderImage()).thenReturn(Optional.of(imageFile.toFile()));

        final String result = headerImageData.dataUri();

        final String expectedBase64 = Base64.getEncoder().encodeToString(imageBytes);
        assertEquals("data:image/png;base64," + expectedBase64, result);
    }

    @Test
    void dataUri_returnsJpegDataUri_whenJpgFileExists(@TempDir Path tempDir) throws IOException {
        final byte[] imageBytes = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
        final Path imageFile = tempDir.resolve("logo.jpg");
        Files.write(imageFile, imageBytes);
        when(headerImageResolver.resolveHeaderImage()).thenReturn(Optional.of(imageFile.toFile()));

        final String result = headerImageData.dataUri();

        assertTrue(result.startsWith("data:image/jpeg;base64,"));
    }

    @Test
    void dataUri_returnsJpegDataUri_whenJpegExtensionUsed(@TempDir Path tempDir) throws IOException {
        final byte[] imageBytes = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
        final Path imageFile = tempDir.resolve("logo.jpeg");
        Files.write(imageFile, imageBytes);
        when(headerImageResolver.resolveHeaderImage()).thenReturn(Optional.of(imageFile.toFile()));

        final String result = headerImageData.dataUri();

        assertTrue(result.startsWith("data:image/jpeg;base64,"));
    }

    @Test
    void dataUri_returnsSvgDataUri_whenSvgFileExists(@TempDir Path tempDir) throws IOException {
        final byte[] imageBytes = "<svg xmlns=\"http://www.w3.org/2000/svg\"/>".getBytes();
        final Path imageFile = tempDir.resolve("logo.svg");
        Files.write(imageFile, imageBytes);
        when(headerImageResolver.resolveHeaderImage()).thenReturn(Optional.of(imageFile.toFile()));

        final String result = headerImageData.dataUri();

        assertTrue(result.startsWith("data:image/svg+xml;base64,"));
    }

    @Test
    void dataUri_returnsGifDataUri_whenGifFileExists(@TempDir Path tempDir) throws IOException {
        final byte[] imageBytes = new byte[]{'G', 'I', 'F', '8'};
        final Path imageFile = tempDir.resolve("logo.gif");
        Files.write(imageFile, imageBytes);
        when(headerImageResolver.resolveHeaderImage()).thenReturn(Optional.of(imageFile.toFile()));

        final String result = headerImageData.dataUri();

        assertTrue(result.startsWith("data:image/gif;base64,"));
    }

    @Test
    void dataUri_returnsBmpDataUri_whenBmpFileExists(@TempDir Path tempDir) throws IOException {
        final byte[] imageBytes = new byte[]{'B', 'M'};
        final Path imageFile = tempDir.resolve("logo.bmp");
        Files.write(imageFile, imageBytes);
        when(headerImageResolver.resolveHeaderImage()).thenReturn(Optional.of(imageFile.toFile()));

        final String result = headerImageData.dataUri();

        assertTrue(result.startsWith("data:image/bmp;base64,"));
    }

    @Test
    void dataUri_defaultsToPng_whenExtensionIsUnrecognized(@TempDir Path tempDir) throws IOException {
        final byte[] imageBytes = new byte[]{1, 2, 3};
        final Path imageFile = tempDir.resolve("logo.unknown");
        Files.write(imageFile, imageBytes);
        when(headerImageResolver.resolveHeaderImage()).thenReturn(Optional.of(imageFile.toFile()));

        final String result = headerImageData.dataUri();

        assertTrue(result.startsWith("data:image/png;base64,"));
    }

    @Test
    void dataUri_returnsNull_whenFileIsEmpty(@TempDir Path tempDir) throws IOException {
        final Path imageFile = tempDir.resolve("empty.png");
        Files.write(imageFile, new byte[0]);
        when(headerImageResolver.resolveHeaderImage()).thenReturn(Optional.of(imageFile.toFile()));

        assertNull(headerImageData.dataUri());
    }
}