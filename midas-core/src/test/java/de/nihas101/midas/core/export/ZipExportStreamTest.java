package de.nihas101.midas.core.export;

import de.nihas101.midas.api.export.ZipExport;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ZipExportStreamTest {

    @Test
    void testNameWithNonNullDates() {

        final LocalDate start = LocalDate.of(2023, 1, 1);
        final LocalDate end = LocalDate.of(2023, 12, 31);
        final ZipExport file = new ZipExportStream(
                start,
                end,
                Mockito.mock(OutputStream.class)
        );
        assertEquals("export_2023-01-01_2023-12-31.zip", file.name());
    }

    @Test
    void testNameWithNullStartDate() {
        final ZipExport file = new ZipExportStream(
                null,
                LocalDate.of(2023, 12, 31),
                Mockito.mock(OutputStream.class)
        );
        assertEquals("export__2023-12-31.zip", file.name());
    }

    @Test
    void testNameWithNullEndDate() {
        final ZipExport file = new ZipExportStream(
                LocalDate.of(2023, 1, 1),
                null,
                Mockito.mock(OutputStream.class)
        );
        assertEquals("export_2023-01-01_.zip", file.name());
    }

    @Test
    void testMimeType() {
        final ZipExport file = new ZipExportStream(
                null,
                null,
                Mockito.mock(OutputStream.class)
        );
        assertEquals("application/zip", file.mimeType());
    }

    @Test
    void testNullOutputStreamThrows() {
        assertThrows(NullPointerException.class, () -> new ZipExportStream(null, null, null));
    }

    @Test
    void testPutEntryAndCloseWritesValidZip() throws Exception {
        final byte[] zipBytes = zipBytes();
        assertNotNull(zipBytes);

        try (final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            assertNextEntry(zis, "test1.txt", "Hello World");
            assertNextEntry(zis, "nested/test2.txt", "Second File");
            assertNull(zis.getNextEntry());
        }
    }

    private static void assertNextEntry(
            final ZipInputStream zis,
            final String filePath,
            final String expectedContent
    ) throws IOException {
        final ZipEntry entry1 = zis.getNextEntry();
        assertNotNull(entry1);
        assertEquals(filePath, entry1.getName());
        assertEquals(expectedContent, new String(zis.readAllBytes(), StandardCharsets.UTF_8));
    }

    private static byte[] zipBytes() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final ZipExport zipExport = new ZipExportStream(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31), baos)
        ) {
            final byte[] content1 = "Hello World".getBytes(StandardCharsets.UTF_8);
            zipExport.putEntry("test1.txt", content1);

            final byte[] content2 = "Second File".getBytes(StandardCharsets.UTF_8);
            zipExport.putEntry("nested/test2.txt", content2);
        }

        return baos.toByteArray();
    }
}
