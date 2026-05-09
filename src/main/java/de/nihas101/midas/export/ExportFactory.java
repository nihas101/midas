package de.nihas101.midas.export;

import de.nihas101.midas.accountstatement.row.AccountStatementRowService;
import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.bookings.row.BookingRowService;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.export.accountstatement.AccountStatementExportDataSource;
import de.nihas101.midas.export.accountstatement.AccountStatementsRowExtractor;
import de.nihas101.midas.export.bookings.BookingsExportDataSource;
import de.nihas101.midas.export.bookings.BookingsRowExtractor;
import de.nihas101.midas.export.interest.InterestExportDataSource;
import de.nihas101.midas.export.interest.InterestRowExtractor;
import de.nihas101.midas.export.pdf.PdfExporter;
import de.nihas101.midas.export.pdf.PdfService;
import de.nihas101.midas.export.xlsx.XlsxExporter;
import de.nihas101.midas.interest.row.InterestRowService;
import de.nihas101.midas.interest.service.InterestBookingsService;
import de.nihas101.midas.interest.service.InterestRateService;
import de.nihas101.midas.openingbalance.service.DefaultOpeningBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ExportFactory {

    private final BookingsReader bookingsReader;
    private final DefaultOpeningBalanceService openingBalanceService;
    private final InterestRateService interestRateService;
    private final AccountStatementService accountStatementService;
    private final MessageSource messageSource;
    private final PdfService pdfService;
    private final BookingRowService bookingRowService;
    private final AccountStatementRowService accountStatementRowService;
    private final InterestRowService interestRowService;
    private final InterestBookingsService interestBookingsService;

    public Export createXlsxExport(
            final ExportRequest request,
            final OutputStream outputStream,
            final Locale locale
    ) {
        final List<ExportDataSource> dataSources = new ArrayList<>();

        // TODO: Don't do this via strings
        if (request.views().contains("bookings")) {
            dataSources.add(
                    new BookingsExportDataSource(
                            new BookingsRowExtractor(
                                    request.shareholders(),
                                    request.startDate(),
                                    request.endDate(),
                                    bookingsReader,
                                    openingBalanceService,
                                    messageSource,
                                    locale
                            ),
                            messageSource,
                            locale
                    )
            );
        }

        if (request.views().contains("interest")) {
            dataSources.add(
                    new InterestExportDataSource(
                            new InterestRowExtractor(
                                    request.shareholders(),
                                    request.startDate(),
                                    request.endDate(),
                                    bookingsReader,
                                    interestRateService
                            ),
                            messageSource,
                            locale
                    )
            );
        }

        if (request.views().contains("account-statements")) {
            dataSources.add(
                    new AccountStatementExportDataSource(
                            new AccountStatementsRowExtractor(
                                    request.shareholders(),
                                    request.startDate(),
                                    request.endDate(),
                                    accountStatementService,
                                    messageSource,
                                    locale
                            ),
                            messageSource,
                            locale
                    )
            );
        }

        return new XlsxExporter(dataSources, outputStream);
    }

    public Export createPdfExport(
            final ExportRequest request,
            final OutputStream outputStream,
            final Locale locale
    ) {
        return new PdfExporter(
                request,
                outputStream,
                locale,
                pdfService,
                interestBookingsService,
                interestRateService,
                accountStatementService,
                messageSource,
                bookingRowService,
                accountStatementRowService,
                interestRowService
        );
    }

}
