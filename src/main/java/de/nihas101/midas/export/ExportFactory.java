package de.nihas101.midas.export;

import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.interest.service.InterestRateService;
import de.nihas101.midas.openingbalance.service.DefaultOpeningBalanceService;
import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Factory for creating Export instances.
 * Orchestrates the selection of data sources and the specific exporter implementation.
 */
@Service
@RequiredArgsConstructor
public class ExportFactory {

    private final BookingsReader bookingsReader;
    private final DefaultOpeningBalanceService openingBalanceService;
    private final InterestRateService interestRateService;
    private final AccountStatementService accountStatementService;
    private final MessageSource messageSource;

    /**
     * Creates an Excel (XLSX) export.
     *
     * @param request      The export parameters from the UI.
     * @param outputStream The stream to write the workbook to.
     * @param locale       The locale for translations.
     * @return An Export instance ready to be triggered.
     */
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
                            request.shareholders(),
                            request.startDate(),
                            request.endDate(),
                            bookingsReader,
                            openingBalanceService,
                            messageSource,
                            locale
                    )
            );
        }

        if (request.views().contains("interest")) {
            dataSources.add(
                    new InterestExportDataSource(
                            request.shareholders(),
                            request.startDate(),
                            request.endDate(),
                            bookingsReader,
                            interestRateService
                    )
            );
        }

        if (request.views().contains("account-statements")) {
            dataSources.add(
                    new AccountStatementExportDataSource(
                            request.shareholders(),
                            request.startDate(),
                            request.endDate(),
                            accountStatementService,
                            messageSource,
                            locale
                    )
            );
        }

        return new XlsxExporter(
                dataSources,
                outputStream,
                messageSource,
                locale
        );
    }

    /**
     * DTO for transport of export parameters from the UI to the factory.
     */
    public record ExportRequest(
            List<Shareholder> shareholders,
            Set<String> views,
            LocalDate startDate,
            LocalDate endDate,
            Set<String> formats
    ) {
    }
}
