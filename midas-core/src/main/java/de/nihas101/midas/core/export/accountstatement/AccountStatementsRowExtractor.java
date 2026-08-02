package de.nihas101.midas.core.export.accountstatement;

import de.nihas101.midas.core.accountstatement.runningtotal.RunningTotalAccountStatement;
import de.nihas101.midas.core.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.core.accountstatement.service.RunningTotalAccountStatementService;
import de.nihas101.midas.core.export.sort.DisplayIdExportRowSort;
import de.nihas101.midas.core.export.sort.ExportRowSort;
import de.nihas101.midas.core.shareholders.dto.Shareholder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.math.BigDecimal.ZERO;

@RequiredArgsConstructor
public class AccountStatementsRowExtractor {
    private final List<Shareholder> shareholders;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final RunningTotalAccountStatementService runningTotalAccountStatementService;
    private final MessageSource messageSource;
    private final Locale locale;
    private final ExportRowSort exportRowSort;

    public AccountStatementsRowExtractor(
            final List<Shareholder> shareholders,
            final LocalDate startDate,
            final LocalDate endDate,
            final RunningTotalAccountStatementService runningTotalAccountStatementService,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                shareholders,
                startDate,
                endDate,
                runningTotalAccountStatementService,
                messageSource,
                locale,
                new DisplayIdExportRowSort()
        );
    }

    public List<ExportRow> rows() {
        return shareholders.stream()
                .map(shareholder -> IntStream.rangeClosed(startDate.getYear(), endDate.getYear())
                        .mapToObj(yearValue -> rowsForYear(shareholder, yearValue)).flatMap(Collection::stream)
                        .toList())
                .flatMap(Collection::stream)
                .sorted(exportRowSort)
                .collect(Collectors.toList());
    }

    private List<ExportRow> rowsForYear(
            final Shareholder shareholder,
            final int yearValue
    ) {
        final Year year = Year.of(yearValue);
        final RunningTotalAccountStatements statements = runningTotalAccountStatementService.runningTotalAccountStatements(
                shareholder,
                year,
                messageSource,
                locale
        );
        final String shareholderName = shareholder.getFirstName() + " " + shareholder.getLastName();

        return statements.runningTotalAccountStatements()
                .stream()
                .filter(stmt -> isWithinRange(stmt.date()))
                .filter(stmt -> !stmt.isHidden())
                .map(stmt -> exportRow(
                        shareholder.getDisplayId(),
                        shareholderName,
                        stmt
                )).toList();
    }

    private ExportRow exportRow(
            final Integer shareholderId,
            final String shareholderName,
            final RunningTotalAccountStatement stmt
    ) {
        final BigDecimal amount = stmt.amount().toBigDecimal();
        final BigDecimal debit = amount.compareTo(ZERO) < 0
                ? amount.abs()
                : ZERO.setScale(2, RoundingMode.HALF_UP);
        final BigDecimal credit = amount.compareTo(ZERO) >= 0
                ? amount
                : ZERO.setScale(2, RoundingMode.HALF_UP);
        return new ExportRow(
                shareholderId,
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