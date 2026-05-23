package de.nihas101.midas.export.pdf;

import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
public class MultiPdfGenerator implements PdfGenerator {

    private final ExportRequest request;
    private final PdfService pdfService;
    private final Locale locale;
    private final OutputStream outputStream;
    private final PdfViewDataExtractor pdfViewDataExtractor;

    public void generate() {
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            generatePdfPerShareholder(zos);
        } catch (IOException e) {
            log.error("Failed to generate ZIP of PDFs", e);
            throw new RuntimeException("Export failed", e);
        }
    }

    private void generatePdfPerShareholder(final ZipOutputStream zos) throws IOException {
        for (Shareholder shareholder : request.shareholders()) {
            generatePdfPerView(zos, shareholder);
        }
    }

    private void generatePdfPerView(final ZipOutputStream zos, final Shareholder shareholder) throws IOException {
        for (String view : request.views()) {
            final PdfViewData data = pdfViewDataExtractor.extractData(shareholder, view);
            final String filename = String.format(
                    "%s_%s_%s-%s.pdf",
                    view,
                    (shareholder.getFirstName() + "_" + shareholder.getLastName()).replace(" ", "_"),
                    request.startDate() != null ? request.startDate().toString() : "",
                    request.endDate() != null ? request.endDate().toString() : ""
            );

            zos.putNextEntry(new ZipEntry(filename));

            // We need a temporary buffer because PdfService writes to the stream
            // and we don't want it to close the ZipOutputStream prematurely
            // (though OpenHTMLToPDF shouldn't close it, it's safer this way
            // if we wanted to be absolutely sure, but ZipOutputStream expects entries)
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            pdfService.generatePdf(data, locale, baos);
            zos.write(baos.toByteArray());

            zos.closeEntry();
        }
    }
}