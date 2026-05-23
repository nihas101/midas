package de.nihas101.midas.export.pdf;

import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class SinglePdfGenerator implements PdfGenerator {

    private final ExportRequest request;
    private final PdfService pdfService;
    private final Locale locale;
    private final OutputStream outputStream;
    private final PdfViewDataExtractor pdfViewDataExtractor;

    @Override
    public void generate() {
        final Shareholder shareholder = request.shareholders().getFirst();
        final String view = request.views().iterator().next();
        final PdfViewData data = pdfViewDataExtractor.extractData(shareholder, view);
        pdfService.generatePdf(data, locale, outputStream);
    }
}