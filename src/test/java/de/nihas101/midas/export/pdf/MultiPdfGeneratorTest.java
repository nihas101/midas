package de.nihas101.midas.export.pdf;

import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.shareholders.dto.Shareholder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MultiPdfGeneratorTest {

    @Mock
    ExportRequest request;

    @Mock
    PdfService pdfService;

    @Mock
    PdfViewDataExtractor pdfViewDataExtractor;

    @Mock
    Shareholder shareholder;

    @Mock
    PdfViewData pdfViewData;

    @InjectMocks
    MultiPdfGenerator generator;

    @Test
    void generate_createsZipWithCorrectEntries() throws Exception {
        when(request.shareholders()).thenReturn(Collections.singletonList(shareholder));
        when(request.views()).thenReturn(Set.of("sampleView"));
        when(request.startDate()).thenReturn(Year.of(2026).atMonth(Month.JANUARY).atDay(1));
        when(request.endDate()).thenReturn(Year.of(2026).atMonth(Month.DECEMBER).atEndOfMonth());
        when(shareholder.getFirstName()).thenReturn("John");
        when(shareholder.getLastName()).thenReturn("Doe");
        when(pdfViewDataExtractor.extractData(eq(shareholder), eq("sampleView"))).thenReturn(pdfViewData);
        // Mock pdfService to write dummy bytes
        doAnswer(invocation -> {
            ByteArrayOutputStream baos = invocation.getArgument(2);
            baos.write(new byte[]{1, 2, 3});
            return null;
        }).when(pdfService).generatePdf(eq(pdfViewData), any(Locale.class), any(ByteArrayOutputStream.class));

        // Use a real ByteArrayOutputStream for the Zip result
        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
        // Recreate generator with real output stream
        generator = new MultiPdfGenerator(
                request,
                pdfService,
                Locale.GERMAN,
                zipOut,
                pdfViewDataExtractor
        );

        generator.generate();

        // Verify service interactions
        verify(pdfViewDataExtractor).extractData(eq(shareholder), eq("sampleView"));
        verify(pdfService).generatePdf(eq(pdfViewData), any(Locale.class), any(ByteArrayOutputStream.class));

        // Read the zip content and verify entry name and content
        try (ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(zipOut.toByteArray()))) {
            ZipEntry entry = zipIn.getNextEntry();
            assertNotNull(entry, "Zip should contain an entry");
            String expectedName = "sampleView_John_Doe_2026-01-01-2026-12-31.pdf";
            assertEquals(expectedName, entry.getName());
            // Ensure there is data inside the entry
            byte[] buffer = new byte[10];
            int read = zipIn.read(buffer);
            assertTrue(read > 0, "Entry should contain data");
            assertNull(zipIn.getNextEntry(), "Only one entry expected in this test");
        }
    }

    @Test
    void generate_propagatesPdfExportException() {
        when(request.shareholders()).thenReturn(Collections.singletonList(shareholder));
        when(request.views()).thenReturn(Set.of("view"));
        when(request.startDate()).thenReturn(LocalDate.now());
        when(request.endDate()).thenReturn(LocalDate.now().plusDays(1));
        when(pdfViewDataExtractor.extractData(any(), any())).thenReturn(pdfViewData);
        doThrow(new PdfExportException("failed", new RuntimeException()))
                .when(pdfService).generatePdf(any(), any(), any());
        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
        generator = new MultiPdfGenerator(request, pdfService, Locale.US, zipOut, pdfViewDataExtractor);
        assertThrows(PdfExportException.class, () -> generator.generate());
    }
}