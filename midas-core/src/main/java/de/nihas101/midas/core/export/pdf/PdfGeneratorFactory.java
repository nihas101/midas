package de.nihas101.midas.core.export.pdf;

import de.nihas101.midas.core.export.ExportRequest;
import lombok.RequiredArgsConstructor;

import java.io.OutputStream;
import java.util.Locale;

@RequiredArgsConstructor
public class PdfGeneratorFactory {

    private final ExportRequest request;
    private final PdfService pdfService;
    private final Locale locale;
    private final OutputStream outputStream;
    private final PdfViewDataExtractor pdfViewDataExtractor;
    private final int filesCount;

    public PdfGeneratorFactory(
            final ExportRequest request,
            final PdfService pdfService,
            final Locale locale,
            final OutputStream outputStream,
            final PdfViewDataExtractor pdfViewDataExtractor
    ) {
        this(
                request,
                pdfService,
                locale,
                outputStream,
                pdfViewDataExtractor,
                startYear(request),
                endYear(request),
                request.shareholders().size(),
                request.views().size()
        );
    }

    private PdfGeneratorFactory(
            final ExportRequest request,
            final PdfService pdfService,
            final Locale locale,
            final OutputStream outputStream,
            final PdfViewDataExtractor pdfViewDataExtractor,
            final int startYear,
            final int endYear,
            final int shareholdersCount,
            final int viewsCount
    ) {
        this(
                request,
                pdfService,
                locale,
                outputStream,
                pdfViewDataExtractor,
                endYear - startYear + 1,
                shareholdersCount,
                viewsCount
        );
    }

    private PdfGeneratorFactory(
            final ExportRequest request,
            final PdfService pdfService,
            final Locale locale,
            final OutputStream outputStream,
            final PdfViewDataExtractor pdfViewDataExtractor,
            final int years,
            final int shareholdersCount,
            final int viewsCount
    ) {
        this(
                request,
                pdfService,
                locale,
                outputStream,
                pdfViewDataExtractor,
                shareholdersCount * viewsCount * years
        );
    }

    private static int startYear(final ExportRequest request) {
        return request.startDate() != null
                ? request.startDate().getYear()
                : 0;
    }

    private static int endYear(final ExportRequest request) {
        return request.endDate() != null
                ? request.endDate().getYear()
                : startYear(request);
    }

    public PdfGenerator createPdfGenerator() {
        if (filesCount <= 0) {
            throw new IllegalArgumentException("At least one shareholder and view are required for the PDF export");
        } else if (filesCount == 1) {
            return new SinglePdfGenerator(
                    request,
                    pdfService,
                    locale,
                    outputStream,
                    pdfViewDataExtractor
            );
        } else {
            return new MultiPdfGenerator(
                    request,
                    pdfService,
                    locale,
                    outputStream,
                    pdfViewDataExtractor
            );
        }
    }
}