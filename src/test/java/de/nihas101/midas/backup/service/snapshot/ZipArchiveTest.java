package de.nihas101.midas.backup.service.snapshot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ZipArchiveTest {

    private ZipArchive zipArchive;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ZipOutputStream zos = new ZipOutputStream(baos);
        zipArchive = new ZipArchive(baos, zos);
    }

    @Test
    void addFile() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "Hello World");

        zipArchive.add(testFile.toFile());
        byte[] result = zipArchive.byteArray();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(result))) {
            ZipEntry entry = zis.getNextEntry();
            Assertions.assertNotNull(entry);
            Assertions.assertEquals("test.txt", entry.getName());
            Assertions.assertEquals("Hello World", new String(zis.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    @Test
    void addFileWithCustomName() throws IOException {
        Path testFile = tempDir.resolve("original.txt");
        Files.writeString(testFile, "Content");

        zipArchive.add(testFile.toFile(), "renamed.txt");
        byte[] result = zipArchive.byteArray();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(result))) {
            ZipEntry entry = zis.getNextEntry();
            Assertions.assertNotNull(entry);
            Assertions.assertEquals("renamed.txt", entry.getName());
        }
    }

    @Test
    void addProperties() throws IOException {
        Properties props = new Properties();
        props.setProperty("key", "value");

        zipArchive.add(props);
        byte[] result = zipArchive.byteArray();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(result))) {
            ZipEntry entry = zis.getNextEntry();
            Assertions.assertNotNull(entry);
            Assertions.assertEquals("application.properties", entry.getName());

            Properties loadedProps = new Properties();
            loadedProps.load(zis);
            Assertions.assertEquals("value", loadedProps.getProperty("key"));
        }
    }

    @Test
    void closeCallsStreams() throws Exception {
        ByteArrayOutputStream mockBaos = mock(ByteArrayOutputStream.class);
        ZipOutputStream mockZos = mock(ZipOutputStream.class);
        ZipArchive archive = new ZipArchive(mockBaos, mockZos);

        archive.close();

        verify(mockBaos).close();
        verify(mockZos).close();
    }
}
