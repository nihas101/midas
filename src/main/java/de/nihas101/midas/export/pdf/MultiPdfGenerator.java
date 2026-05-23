package de.nihas101.midas.export.pdf;

import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.export.ExportViews;
import de.nihas101.midas.export.LocalizedExportView;
import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
public class MultiPdfGenerator implements PdfGenerator {

    private final List<Shareholder> shareholders;
    private final ExportViews views;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final PdfService pdfService;
    private final Locale locale;
    private final OutputStream outputStream;
    private final PdfViewDataExtractor pdfViewDataExtractor;

    public MultiPdfGenerator(
            final ExportRequest request,
            final PdfService pdfService,
            final Locale locale,
            final OutputStream outputStream,
            final PdfViewDataExtractor pdfViewDataExtractor
    ) {
        this(
                request.shareholders(),
                request.views(),
                request.startDate(),
                request.endDate(),
                pdfService,
                locale,
                outputStream,
                pdfViewDataExtractor
        );
    }

    public void generate() {
        try (final ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            generatePdfPerShareholder(zos);
        } catch (IOException e) {
            log.error("Failed to generate ZIP of PDFs", e);
            throw new RuntimeException("Export failed", e);
        }
    }

    @Override
    public String fileName() {
        return "export_" + startDate + "_" + endDate + ".zip";
    }

    @Override
    public String mimeType() {
        return "application/zip";
    }

    private void generatePdfPerShareholder(final ZipOutputStream zos) throws IOException {
        for (final Shareholder shareholder : shareholders) {
            generatePdfPerView(zos, shareholder);
        }
    }

    private void generatePdfPerView(final ZipOutputStream zos, final Shareholder shareholder) throws IOException {
        for (final LocalizedExportView view : views.iterator()) {
            createEntryForView(zos, shareholder, view);
        }
    }

    private void createEntryForView(
            final ZipOutputStream zos,
            final Shareholder shareholder,
            final LocalizedExportView view
    ) throws IOException {
        final PdfFile pdfFile = new PdfFile(
                shareholder,
                view.localizedName(),
                startDate,
                endDate
        );
        zos.putNextEntry(new ZipEntry(pdfFile.name()));


        // We need a temporary buffer because PdfService writes to the stream
        // and we don't want it to close the ZipOutputStream prematurely
        // (though OpenHTMLToPDF shouldn't close it, it's safer this way
        // if we wanted to be absolutely sure, but ZipOutputStream expects entries)
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new SinglePdfGenerator(
                shareholder,
                view,
                startDate,
                endDate,
                pdfService,
                locale,
                baos,
                pdfViewDataExtractor
        ).generate();
        zos.write(baos.toByteArray());
        zos.closeEntry();
    }
}