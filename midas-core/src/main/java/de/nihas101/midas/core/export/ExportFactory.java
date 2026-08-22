package de.nihas101.midas.core.export;

import de.nihas101.midas.api.accountstatement.AccountStatementRowService;
import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.api.export.Export;
import de.nihas101.midas.api.export.ExportDataSource;
import de.nihas101.midas.api.interest.InterestBookingsService;
import de.nihas101.midas.api.interest.InterestRowService;
import de.nihas101.midas.core.accountstatement.service.RunningTotalAccountStatementService;
import de.nihas101.midas.core.bookings.row.BookingRowService;
import de.nihas101.midas.core.config.DatesConfig;
import de.nihas101.midas.core.export.accountstatement.AccountStatementExportDataSource;
import de.nihas101.midas.core.export.accountstatement.AccountStatementsRowExtractor;
import de.nihas101.midas.core.export.bookings.BookingsExportDataSource;
import de.nihas101.midas.core.export.bookings.BookingsRowExtractor;
import de.nihas101.midas.core.export.interest.InterestExportDataSource;
import de.nihas101.midas.core.export.interest.InterestRowExtractor;
import de.nihas101.midas.core.export.pdf.PdfExporter;
import de.nihas101.midas.core.export.pdf.PdfService;
import de.nihas101.midas.core.export.xlsx.XlsxExporter;
import de.nihas101.midas.core.export.xlsx.XslxFile;
import de.nihas101.midas.core.interest.service.InterestRateService;
import de.nihas101.midas.core.openingbalance.service.DefaultOpeningBalanceService;
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
    private final RunningTotalAccountStatementService runningTotalAccountStatementService;
    private final MessageSource messageSource;
    private final PdfService pdfService;
    private final BookingRowService bookingRowService;
    private final AccountStatementRowService accountStatementRowService;
    private final InterestRowService interestRowService;
    private final InterestBookingsService interestBookingsService;
    private final DatesConfig datesConfig;

    public Export createXlsxExport(
            final ExportRequest request,
            final OutputStream outputStream,
            final Locale locale
    ) {
        final List<ExportDataSource> dataSources = new ArrayList<>();

        if (request.views().contains(ExportViewName.BOOKINGS)) {
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

        if (request.views().contains(ExportViewName.INTEREST)) {
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

        if (request.views().contains(ExportViewName.ACCOUNT_STATEMENTS)) {
            dataSources.add(
                    new AccountStatementExportDataSource(
                            new AccountStatementsRowExtractor(
                                    request.shareholders(),
                                    request.startDate(),
                                    request.endDate(),
                                    runningTotalAccountStatementService,
                                    messageSource,
                                    locale
                            ),
                            messageSource,
                            locale
                    )
            );
        }

        return new XlsxExporter(
                dataSources,
                outputStream,
                new XslxFile(
                        request.startDate(),
                        request.endDate()
                ),
                datesConfig
        );
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
                bookingsReader,
                interestBookingsService,
                interestRateService,
                runningTotalAccountStatementService,
                messageSource,
                bookingRowService,
                accountStatementRowService,
                interestRowService
        );
    }

}
