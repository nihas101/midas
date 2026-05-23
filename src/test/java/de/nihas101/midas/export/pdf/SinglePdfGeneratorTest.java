package de.nihas101.midas.export.pdf;

import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.shareholders.dto.Shareholder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.OutputStream;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SinglePdfGeneratorTest {

    @Mock
    ExportRequest request;

    @Mock
    PdfService pdfService;

    @Mock
    PdfViewDataExtractor pdfViewDataExtractor;

    @Mock
    OutputStream outputStream;

    @Mock
    Shareholder shareholder;

    @Mock
    PdfViewData pdfViewData;

    @Mock
    Locale locale;

    @InjectMocks
    SinglePdfGenerator generator;

    @Test
    void generate_callsPdfServiceWithExtractedData() {
        // Setup request mock
        when(request.shareholders()).thenReturn(Collections.singletonList(shareholder));
        when(request.views()).thenReturn(Set.of("sample-view"));
        when(pdfViewDataExtractor.extractData(eq(shareholder), eq("sample-view"))).thenReturn(pdfViewData);

        // Execute generate
        generator.generate();

        // Verify interactions
        verify(pdfViewDataExtractor).extractData(eq(shareholder), eq("sample-view"));
        verify(pdfService).generatePdf(eq(pdfViewData), eq(locale), eq(outputStream));
    }

    @Test
    void generate_propagatesPdfExportException() {
        when(request.shareholders()).thenReturn(Collections.singletonList(shareholder));
        when(request.views()).thenReturn(Set.of("view"));
        when(pdfViewDataExtractor.extractData(any(), any())).thenReturn(pdfViewData);
        doThrow(new PdfExportException("dummy exception", new RuntimeException()))
                .when(pdfService).generatePdf(any(), any(), any());

        assertThrows(PdfExportException.class, () -> generator.generate());
    }
}