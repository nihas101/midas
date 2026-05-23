package de.nihas101.midas.export.pdf;

import de.nihas101.midas.shareholders.dto.Shareholder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PdfFileTest {

    @Mock
    private Shareholder shareholder;

    @Test
    public void testNameAllFieldsPresent() {
        when(shareholder.getFirstName()).thenReturn("John");
        when(shareholder.getLastName()).thenReturn("Doe");
        when(shareholder.getDisplayId()).thenReturn(42);
        when(shareholder.getId()).thenReturn(1001);
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);
        PdfFile pdfFile = new PdfFile(shareholder, "overview", start, end);
        String expected = "John_Doe_(42-1001)_overview_2023-01-01_2023-12-31.pdf";
        assertEquals(expected, pdfFile.name());
    }

    @Test
    public void testNameWithNullFields() {
        when(shareholder.getFirstName()).thenReturn(null);
        when(shareholder.getLastName()).thenReturn(null);
        when(shareholder.getDisplayId()).thenReturn(null);
        when(shareholder.getId()).thenReturn(null);
        PdfFile pdfFile = new PdfFile(shareholder, null, null, null);
        String expected = "__(-)___.pdf"; // all placeholders empty
        assertEquals(expected, pdfFile.name());
    }

    @Test
    public void testMimeType() {
        PdfFile pdfFile = new PdfFile(null, null, null, null);
        assertEquals("application/pdf", pdfFile.mimeType());
    }
}