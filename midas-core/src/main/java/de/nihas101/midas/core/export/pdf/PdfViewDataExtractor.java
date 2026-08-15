package de.nihas101.midas.core.export.pdf;

import de.nihas101.midas.api.accountstatement.AccountStatementRowService;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatements;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.api.interest.InterestBookingsReader;
import de.nihas101.midas.api.interest.InterestCalculation;
import de.nihas101.midas.api.interest.InterestRowService;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.accountstatement.service.RunningTotalAccountStatementService;
import de.nihas101.midas.core.bookings.row.BookingRowService;
import de.nihas101.midas.core.export.ExportRequest;
import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.interest.DefaultInterestCalculation;
import de.nihas101.midas.core.interest.dto.InterestRate;
import de.nihas101.midas.core.interest.service.InterestRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class PdfViewDataExtractor {

    private final ExportRequest request;
    private final MessageSource messageSource;
    private final Locale locale;
    private final BookingsReader bookingsReader;
    private final BookingRowService bookingRowService;
    private final RunningTotalAccountStatementService runningTotalAccountStatementService;
    private final AccountStatementRowService accountStatementRowService;
    private final InterestRateService interestRateService;
    private final InterestBookingsReader interestBookingsReader;
    private final InterestRowService interestRowService;

    public PdfViewData extractData(
            final Shareholder shareholder,
            final ExportViewName view
    ) {
        switch (view) {
            case BOOKINGS -> {
                return extractBookingsData(shareholder);
            }
            case ACCOUNT_STATEMENTS -> {
                return extractAccountStatementsData(shareholder);
            }
            case INTEREST -> {
                return extractInterestData(shareholder);
            }
            case null, default -> {
                final Integer year = request.startDate().getYear();
                return new PdfViewData(
                        view,
                        shareholder.getFirstName() + " " + shareholder.getLastName(),
                        shareholder,
                        year,
                        null,
                        List.of(),
                        List.of()
                );
            }
        }
    }

    private PdfViewData extractBookingsData(final Shareholder shareholder) {
        final List<String> headers = List.of(
                messageSource.getMessage("export.pdf.bookings.table.id", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.date", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.comment", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.total", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.withdrawal", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.tax-previous-year", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.tax-credit", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.interest", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.compensation", null, locale),
                messageSource.getMessage("export.pdf.bookings.table.balance", null, locale)
        );

        return new PdfViewData(
                ExportViewName.BOOKINGS,
                shareholder.getFirstName() + " " + shareholder.getLastName(),
                shareholder,
                Year.of(request.startDate().getYear()).getValue(),
                null,
                headers,
                // Thymeleaf requires an ArrayList
                new ArrayList<>(
                        bookingRowService.generateRows(
                                bookingsReader.bookingsForShareholderAndDates(
                                        shareholder.getId(),
                                        request.startDate(),
                                        request.endDate()
                                ),
                                locale
                        )
                )
        );
    }

    private PdfViewData extractAccountStatementsData(final Shareholder shareholder) {
        List<String> headers = List.of(
                messageSource.getMessage("account-statements.table.date", null, locale),
                messageSource.getMessage("account-statements.table.type", null, locale),
                messageSource.getMessage("account-statements.table.debit", null, locale),
                messageSource.getMessage("account-statements.table.credit", null, locale),
                messageSource.getMessage("account-statements.table.balance", null, locale)
        );

        final Year year = Year.of(request.startDate().getYear());
        final RunningTotalAccountStatements statements = runningTotalAccountStatementService.runningTotalAccountStatements(
                shareholder,
                year,
                messageSource,
                locale
        );

        final List<Object> rows = new ArrayList<>(accountStatementRowService.generateRows(statements, false));
        rows.add(accountStatementRowService.generateClosingRow(statements, locale));

        return new PdfViewData(
                ExportViewName.ACCOUNT_STATEMENTS,
                shareholder.getFirstName() + " " + shareholder.getLastName(),
                shareholder,
                year.getValue(),
                null,
                headers,
                rows
        );
    }

    private PdfViewData extractInterestData(final Shareholder shareholder) {
        final List<String> headers = List.of(
                messageSource.getMessage("interest.table.month", null, locale),
                messageSource.getMessage("interest.table.transactions", null, locale),
                messageSource.getMessage("interest.table.sh", null, locale),
                messageSource.getMessage("interest.table.balance", null, locale),
                messageSource.getMessage("interest.table.sh", null, locale),
                messageSource.getMessage("interest.table.days", null, locale),
                messageSource.getMessage("interest.table.interest-amount", null, locale)
        );

        final Year year = Year.of(request.startDate().getYear());
        final InterestRate rate = interestRateService.interestRate(shareholder.getId(), year);
        final BigDecimal interestRate = rate != null ? rate.getInterestRate() : BigDecimal.ZERO;

        final Bookings bookings = interestBookingsReader.interestRelatedBookingsForShareholderAndYear(shareholder.getId(), year);
        final InterestCalculation interestCalculation = new DefaultInterestCalculation(bookings, year, interestRate);

        final List<Object> rows = new ArrayList<>(interestRowService.generateRows(year, bookings, interestRate, interestCalculation, locale));

        return new PdfViewData(
                ExportViewName.INTEREST,
                shareholder.getFirstName() + " " + shareholder.getLastName(),
                shareholder,
                year.getValue(),
                interestRate,
                headers,
                rows
        );
    }
}