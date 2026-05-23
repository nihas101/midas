package de.nihas101.midas.export.pdf;

import de.nihas101.midas.accountstatement.row.AccountStatementRowService;
import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.bookings.row.BookingRowService;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.export.Export;
import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.interest.row.InterestRowService;
import de.nihas101.midas.interest.service.InterestBookingsReader;
import de.nihas101.midas.interest.service.InterestRateService;
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
            final AccountStatementService accountStatementService,
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
                        accountStatementService,
                        accountStatementRowService,
                        interestRateService,
                        interestBookingsReader,
                        interestRowService
                )
        );
    }

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

}
