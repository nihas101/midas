package de.nihas101.midas.core.export.pdf;

import de.nihas101.midas.api.export.ZipExport;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.export.ExportRequest;
import de.nihas101.midas.core.export.ExportViews;
import de.nihas101.midas.core.export.LocalizedExportView;
import de.nihas101.midas.core.export.ZipExportStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class MultiPdfGenerator implements PdfGenerator {

    private final List<Shareholder> shareholders;
    private final ExportViews views;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final PdfService pdfService;
    private final Locale locale;
    private final PdfViewDataExtractor pdfViewDataExtractor;
    private final ZipExport zipExport;

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
                pdfViewDataExtractor,
                new ZipExportStream(
                        request.startDate(),
                        request.endDate(),
                        outputStream
                )
        );
    }

    public void generate() {
        try (final ZipExport zos = zipExport) {
            generatePdfPerShareholder(zos);
        } catch (Exception e) {
            log.error("Failed to generate ZIP of PDFs", e);
            throw new PdfExportException("Export failed", e);
        }
    }

    @Override
    public String fileName() {
        return zipExport.name();
    }

    @Override
    public String mimeType() {
        return zipExport.mimeType();
    }

    private void generatePdfPerShareholder(final ZipExport zos) throws IOException {
        final int startYear = startDate != null ? startDate.getYear() : 0;
        final int endYear = endDate != null ? endDate.getYear() : startYear;

        for (int currentYear = startYear; currentYear <= endYear; currentYear++) {
            final LocalDate yearStart = yearStart(currentYear, startYear);
            final LocalDate yearEnd = yearEnd(currentYear, endYear);
            for (final Shareholder shareholder : shareholders) {
                generatePdfPerView(
                        zos,
                        shareholder,
                        yearStart,
                        yearEnd
                );
            }
        }
    }

    private LocalDate yearEnd(final int currentYear, final int endYear) {
        return (currentYear == endYear && endDate != null)
                ? endDate
                : LocalDate.of(currentYear, 12, 31);
    }

    private LocalDate yearStart(final int currentYear, final int startYear) {
        return (currentYear == startYear && startDate != null)
                ? startDate
                : LocalDate.of(currentYear, 1, 1);
    }

    private void generatePdfPerView(
            final ZipExport zos,
            final Shareholder shareholder,
            final LocalDate currentStartDate,
            final LocalDate currentEndDate
    ) throws IOException {
        for (final LocalizedExportView view : views.iterator()) {
            createEntryForView(
                    zos,
                    shareholder,
                    view,
                    currentStartDate,
                    currentEndDate
            );
        }
    }

    private void createEntryForView(
            final ZipExport zos,
            final Shareholder shareholder,
            final LocalizedExportView view,
            final LocalDate currentStartDate,
            final LocalDate currentEndDate
    ) throws IOException {
        final PdfFile pdfFile = new PdfFile(
                shareholder,
                view.localizedName(),
                currentStartDate,
                currentEndDate
        );
        zos.putEntry(
                pdfFile.name(),
                singlePdfStream(shareholder, view, currentStartDate, currentEndDate).toByteArray()
        );
    }

    private ByteArrayOutputStream singlePdfStream(
            final Shareholder shareholder,
            final LocalizedExportView view,
            final LocalDate currentStartDate,
            final LocalDate currentEndDate
    ) {
        // We need a temporary buffer because PdfService writes to the stream
        // and we don't want it to close the ZipOutputStream prematurely
        // (though OpenHTMLToPDF shouldn't close it, it's safer this way
        // if we wanted to be absolutely sure, but ZipOutputStream expects entries)
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new SinglePdfGenerator(
                shareholder,
                view,
                currentStartDate,
                currentEndDate,
                pdfService,
                locale,
                baos,
                pdfViewDataExtractor
        ).generate();
        return baos;
    }
}