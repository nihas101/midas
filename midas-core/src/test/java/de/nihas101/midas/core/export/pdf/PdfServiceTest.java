package de.nihas101.midas.core.export.pdf;

import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.export.pdf.config.PdfExportConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Mock
    private HeaderImageResolver headerImageResolver;

    @Mock
    private PdfExportConfig pdfExportConfig;

    @InjectMocks
    private PdfService pdfService;

    private PdfViewData sampleData() {
        return new PdfViewData(
                ExportViewName.BOOKINGS,
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
        when(htmlTemplateEngine.generateHtml(any(), any())).thenReturn("<html><body>Test</body></html>");
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> pdfService.generatePdf(sampleData(), Locale.US, output));

        assertTrue(output.size() > 0, "Output stream should contain PDF bytes");
        verify(htmlTemplateEngine).generateHtml(eq(sampleData()), any());
        verify(fontRegister).registerLiberationSerifFonts(any());
    }

    @Test
    void generatePdf_withValidImage_setsHeaderImageContextVariable() throws IOException {
        when(headerImageResolver.dataUri()).thenReturn("data:image/png;base64,");

        ArgumentCaptor<TemplateContext> contextCaptor = ArgumentCaptor.forClass(TemplateContext.class);
        when(htmlTemplateEngine.generateHtml(any(), contextCaptor.capture())).thenReturn("<html><body>Test</body></html>");
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> pdfService.generatePdf(sampleData(), Locale.US, output));

        TemplateContext capturedContext = contextCaptor.getValue();
        assertNotNull(capturedContext.variables().get("headerImage"));
        assertTrue(capturedContext.variables().get("headerImage").toString().startsWith("data:image/png;base64,"));
    }

    @Test
    void generatePdf_withMissingImage_omitsHeaderImageSilently() {
        ArgumentCaptor<TemplateContext> contextCaptor = ArgumentCaptor.forClass(TemplateContext.class);
        when(htmlTemplateEngine.generateHtml(any(), contextCaptor.capture())).thenReturn("<html><body>Test</body></html>");
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> pdfService.generatePdf(sampleData(), Locale.US, output));

        TemplateContext capturedContext = contextCaptor.getValue();
        assertNull(capturedContext.variables().get("headerImage"));
    }

    @Test
    void generatePdf_templateFailureThrowsPdfExportException() {
        when(htmlTemplateEngine.generateHtml(any(), any())).thenThrow(new RuntimeException("template error"));
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final PdfExportException ex = assertThrows(PdfExportException.class,
                () -> pdfService.generatePdf(sampleData(), Locale.US, output));
        assertTrue(ex.getMessage().contains("Error generating PDF"));
        verify(htmlTemplateEngine).generateHtml(eq(sampleData()), any());
        // Ensure no further interactions when template fails
        verifyNoInteractions(fontRegister);
    }
}