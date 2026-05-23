package de.nihas101.midas.export.pdf;

import de.nihas101.midas.accountstatement.row.AccountStatementRowService;
import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.row.BookingRowService;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.interest.InterestCalculation;
import de.nihas101.midas.interest.dto.InterestRate;
import de.nihas101.midas.interest.row.InterestRowService;
import de.nihas101.midas.interest.service.InterestBookingsReader;
import de.nihas101.midas.interest.service.InterestRateService;
import de.nihas101.midas.shareholders.dto.Shareholder;
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
    private final AccountStatementService accountStatementService;
    private final AccountStatementRowService accountStatementRowService;
    private final InterestRateService interestRateService;
    private final InterestBookingsReader interestBookingsReader;
    private final InterestRowService interestRowService;

    public PdfViewData extractData(
            final Shareholder shareholder,
            final String view
    ) {
        switch (view) {
            case "bookings" -> {
                return extractBookingsData(shareholder);
            }
            case "account-statements" -> {
                return extractAccountStatementsData(shareholder);
            }
            case "interest" -> {
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
                "bookings",
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
                messageSource.getMessage("account-statements.table.id", null, locale),
                messageSource.getMessage("account-statements.table.date", null, locale),
                messageSource.getMessage("account-statements.table.type", null, locale),
                messageSource.getMessage("account-statements.table.debit", null, locale),
                messageSource.getMessage("account-statements.table.credit", null, locale),
                messageSource.getMessage("account-statements.table.balance", null, locale)
        );

        final Year year = Year.of(request.startDate().getYear());
        final RunningTotalAccountStatements statements = accountStatementService.runningTotalAccountStatements(shareholder, year, messageSource, locale);

        final List<Object> rows = new ArrayList<>(accountStatementRowService.generateRows(statements));
        rows.add(accountStatementRowService.generateClosingRow(statements, locale));

        return new PdfViewData(
                "account-statements",
                shareholder.getFirstName() + " " + shareholder.getLastName(),
                shareholder,
                year.getValue(),
                null,
                headers,
                rows
        );
    }

    private PdfViewData extractInterestData(final Shareholder shareholder) {
        List<String> headers = List.of(
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
        final InterestCalculation interestCalculation = new InterestCalculation(bookings, year, interestRate);

        final List<Object> rows = new ArrayList<>(interestRowService.generateRows(year, bookings, interestRate, interestCalculation, locale));

        return new PdfViewData(
                "interest",
                shareholder.getFirstName() + " " + shareholder.getLastName(),
                shareholder,
                year.getValue(),
                interestRate,
                headers,
                rows
        );
    }
}