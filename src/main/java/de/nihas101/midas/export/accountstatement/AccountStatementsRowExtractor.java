package de.nihas101.midas.export.accountstatement;

import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatement;
import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class AccountStatementsRowExtractor {
    private final List<Shareholder> shareholders;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final AccountStatementService accountStatementService;
    private final MessageSource messageSource;
    private final Locale locale;

    public List<ExportRow> rows() {
        return shareholders.stream()
                .map(this::rowsForShareholder)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(ExportRow::shareholderName).thenComparing(ExportRow::date))
                .collect(Collectors.toList());
    }

    private List<ExportRow> rowsForShareholder(final Shareholder shareholder) {

        return IntStream.rangeClosed(startDate.getYear(), endDate.getYear())
                .mapToObj(yearValue ->
                        rowsForYear(shareholder, yearValue)
                ).flatMap(Collection::stream)
                .toList();
    }

    private List<ExportRow> rowsForYear(
            final Shareholder shareholder,
            final int yearValue
    ) {
        final Year year = Year.of(yearValue);
        final RunningTotalAccountStatements statements = accountStatementService.runningTotalAccountStatements(
                shareholder, year, messageSource, locale);
        final String shareholderName = shareholder.getFirstName() + " " + shareholder.getLastName();

        return statements.runningTotalAccountStatements()
                .stream()
                .filter(stmt -> isWithinRange(stmt.date()))
                .map(stmt -> exportRow(shareholderName, stmt)).toList();
    }

    private ExportRow exportRow(final String shareholderName, final RunningTotalAccountStatement stmt) {
        final BigDecimal amount = stmt.amount().toBigDecimal();
        final BigDecimal debit = amount.compareTo(BigDecimal.ZERO) < 0
                ? amount.abs()
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        final BigDecimal credit = amount.compareTo(BigDecimal.ZERO) >= 0
                ? amount
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return new ExportRow(
                shareholderName,
                stmt.id(),
                stmt.date(),
                stmt.label(),
                debit,
                credit,
                stmt.currentBalance().toBigDecimal()
        );
    }

    private boolean isWithinRange(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

}