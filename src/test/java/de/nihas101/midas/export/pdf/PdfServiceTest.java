package de.nihas101.midas.export.pdf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

    @Mock
    private HtmlTemplateEngine htmlTemplateEngine;

    @Mock
    private FontRegister fontRegister;

    @InjectMocks
    private PdfService pdfService;

    private PdfViewData sampleData() {
        return new PdfViewData(
                "sample-view",
                "Sample Shareholder",
                null,
                2023,
                null,
                List.of("Header1", "Header2"),
                List.of(List.of("Row1Col1", "Row1Col2"))
        );
    }

    @Test
    void generatePdf_successfulWritesOutput() throws IOException {
        // Arrange
        when(htmlTemplateEngine.generateHtml(any(), any())).thenReturn("<html><body>Test</body></html>");
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        // Act
        assertDoesNotThrow(() -> pdfService.generatePdf(sampleData(), Locale.US, output));

        // Assert
        assertTrue(output.size() > 0, "Output stream should contain PDF bytes");
        verify(htmlTemplateEngine).generateHtml(eq(sampleData()), any());
        verify(fontRegister).registerLiberationSerifFonts(any());
    }

    @Test
    void generatePdf_templateFailureThrowsPdfExportException() {
        // Arrange
        when(htmlTemplateEngine.generateHtml(any(), any())).thenThrow(new RuntimeException("template error"));
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        // Act & Assert
        final PdfExportException ex = assertThrows(PdfExportException.class,
                () -> pdfService.generatePdf(sampleData(), Locale.US, output));
        assertTrue(ex.getMessage().contains("Error generating PDF"));
        verify(htmlTemplateEngine).generateHtml(eq(sampleData()), any());
        // Ensure no further interactions when template fails
        verifyNoInteractions(fontRegister);
    }
}