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
import java.time.LocalDate;
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
            final ExportViewName view,
            final Year year
    ) {
        switch (view) {
            case BOOKINGS -> {
                return extractBookingsData(shareholder, year);
            }
            case ACCOUNT_STATEMENTS -> {
                return extractAccountStatementsData(shareholder, year);
            }
            case INTEREST -> {
                return extractInterestData(shareholder, year);
            }
            case null, default -> {
                return new PdfViewData(
                        view,
                        shareholder.getFirstName() + " " + shareholder.getLastName(),
                        shareholder,
                        year.getValue(),
                        null,
                        List.of(),
                        List.of()
                );
            }
        }
    }

    private PdfViewData extractBookingsData(final Shareholder shareholder, final Year year) {
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

        final LocalDate yearStart = year.atDay(1);
        final LocalDate yearEnd = year.atMonth(12).atEndOfMonth();

        return new PdfViewData(
                ExportViewName.BOOKINGS,
                shareholder.getFirstName() + " " + shareholder.getLastName(),
                shareholder,
                year.getValue(),
                null,
                headers,
                // Thymeleaf requires an ArrayList
                new ArrayList<>(
                        bookingRowService.generateRows(
                                bookingsReader.bookingsForShareholderAndDates(
                                        shareholder.getId(),
                                        yearStart,
                                        yearEnd
                                ),
                                locale
                        )
                )
        );
    }

    private PdfViewData extractAccountStatementsData(final Shareholder shareholder, final Year year) {
        List<String> headers = List.of(
                messageSource.getMessage("account-statements.table.date", null, locale),
                messageSource.getMessage("account-statements.table.type", null, locale),
                messageSource.getMessage("account-statements.table.debit", null, locale),
                messageSource.getMessage("account-statements.table.credit", null, locale),
                messageSource.getMessage("account-statements.table.balance", null, locale)
        );

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

    private PdfViewData extractInterestData(final Shareholder shareholder, final Year year) {
        final List<String> headers = List.of(
                messageSource.getMessage("interest.table.month", null, locale),
                messageSource.getMessage("interest.table.transactions", null, locale),
                messageSource.getMessage("interest.table.sh", null, locale),
                messageSource.getMessage("interest.table.balance", null, locale),
                messageSource.getMessage("interest.table.sh", null, locale),
                messageSource.getMessage("interest.table.days", null, locale),
                messageSource.getMessage("interest.table.interest-amount", null, locale)
        );

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