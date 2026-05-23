package de.nihas101.midas.export.pdf;

import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.export.ExportViews;
import de.nihas101.midas.export.LocalizedExportView;
import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SinglePdfGenerator implements PdfGenerator {

    private final PdfService pdfService;
    private final Locale locale;
    private final OutputStream outputStream;
    private final PdfViewData data;
    private final PdfFile pdfFile;

    public SinglePdfGenerator(
            final ExportRequest request,
            final PdfService pdfService,
            final Locale locale,
            final OutputStream outputStream,
            final PdfViewDataExtractor pdfViewDataExtractor
    ) {
        this(
                Optional.ofNullable(request)
                        .map(ExportRequest::shareholders)
                        .map(List::getFirst)
                        .orElse(null),
                Optional.ofNullable(request)
                        .map(ExportRequest::views)
                        .map(ExportViews::first)
                        .orElse(null),
                Optional.ofNullable(request)
                        .map(ExportRequest::startDate)
                        .orElse(null),
                Optional.ofNullable(request)
                        .map(ExportRequest::endDate)
                        .orElse(null),
                pdfService,
                locale,
                outputStream,
                pdfViewDataExtractor
        );
    }

    public SinglePdfGenerator(
            final Shareholder shareholder,
            final LocalizedExportView localizedExportView,
            final LocalDate startDate,
            final LocalDate endDate,
            final PdfService pdfService,
            final Locale locale,
            final OutputStream outputStream,
            final PdfViewDataExtractor pdfViewDataExtractor
    ) {
        this(
                pdfService,
                locale,
                outputStream,
                pdfViewDataExtractor.extractData(
                        shareholder,
                        localizedExportView != null ? localizedExportView.internalName() : null
                ),
                new PdfFile(
                        shareholder,
                        localizedExportView != null ? localizedExportView.localizedName() : null,
                        startDate,
                        endDate
                )
        );
    }

    @Override
    public void generate() {
        pdfService.generatePdf(data, locale, outputStream);
    }

    @Override
    public String fileName() {
        return pdfFile.name();
    }

    @Override
    public String mimeType() {
        return pdfFile.mimeType();
    }
}