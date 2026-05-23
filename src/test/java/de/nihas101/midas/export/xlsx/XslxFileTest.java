package de.nihas101.midas.export.xlsx;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XslxFileTest {

    @Test
    public void testNameWithNonNullDates() {
        final LocalDate start = LocalDate.of(2023, 1, 1);
        final LocalDate end = LocalDate.of(2023, 12, 31);
        final XslxFile file = new XslxFile(start, end);
        final String expected = "export_2023-01-01_2023-12-31.xlsx";
        assertEquals(expected, file.name());
    }

    @Test
    public void testNameWithNullStartDate() {
        final XslxFile file = new XslxFile(null, LocalDate.of(2023, 12, 31));
        final String expected = "export__2023-12-31.xlsx";
        assertEquals(expected, file.name());
    }

    @Test
    public void testNameWithNullEndDate() {
        final XslxFile file = new XslxFile(LocalDate.of(2023, 1, 1), null);
        final String expected = "export_2023-01-01_.xlsx";
        assertEquals(expected, file.name());
    }

    @Test
    public void testMimeType() {
        final XslxFile file = new XslxFile(null, null);
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", file.mimeType());
    }
}