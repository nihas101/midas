package de.nihas101.midas.core.export.pdf;

import de.nihas101.midas.api.accountstatement.AccountStatementRowService;
import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.api.export.Export;
import de.nihas101.midas.api.interest.InterestBookingsReader;
import de.nihas101.midas.api.interest.InterestRowService;
import de.nihas101.midas.core.accountstatement.service.RunningTotalAccountStatementService;
import de.nihas101.midas.core.bookings.row.BookingRowService;
import de.nihas101.midas.core.export.ExportRequest;
import de.nihas101.midas.core.interest.service.InterestRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.io.OutputStream;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class PdfExporter implements Export {

    private final PdfGenerator pdfGenerator;

    public PdfExporter(
            final ExportRequest request,
            final OutputStream outputStream,
            final Locale locale,
            final PdfService pdfService,
            final BookingsReader bookingsReader,
            final InterestBookingsReader interestBookingsReader,
            final InterestRateService interestRateService,
            final RunningTotalAccountStatementService runningTotalAccountStatementService,
            final MessageSource messageSource,
            final BookingRowService bookingRowService,
            final AccountStatementRowService accountStatementRowService,
            final InterestRowService interestRowService
    ) {
        this(
                request,
                outputStream,
                locale,
                pdfService,
                new PdfViewDataExtractor(
                        request,
                        messageSource,
                        locale,
                        bookingsReader,
                        bookingRowService,
                        runningTotalAccountStatementService,
                        accountStatementRowService,
                        interestRateService,
                        interestBookingsReader,
                        interestRowService
                )
        );
    }

    // TODO: This fails to consider exports that go over multiple years and would require multiple PDFs even for a single shareholder and view
    private PdfExporter(
            final ExportRequest request,
            final OutputStream outputStream,
            final Locale locale,
            final PdfService pdfService,
            final PdfViewDataExtractor pdfViewDataExtractor
    ) {
        final int totalFiles = request.shareholders().size() * request.views().size();
        if (totalFiles == 0) {
            throw new IllegalArgumentException("At least one shareholder and view are required for the PDF export");
        }
        if (totalFiles == 1) {
            this.pdfGenerator = new SinglePdfGenerator(
                    request,
                    pdfService,
                    locale,
                    outputStream,
                    pdfViewDataExtractor
            );
        } else {
            this.pdfGenerator = new MultiPdfGenerator(
                    request,
                    pdfService,
                    locale,
                    outputStream,
                    pdfViewDataExtractor
            );
        }
    }

    @Override
    public void trigger() {
        pdfGenerator.generate();
    }

    @Override
    public String fileName() {
        return pdfGenerator.fileName();
    }

    @Override
    public String mimeType() {
        return pdfGenerator.mimeType();
    }

}
