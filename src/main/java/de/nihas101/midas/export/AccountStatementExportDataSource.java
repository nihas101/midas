package de.nihas101.midas.export;

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Implementation of ExportDataSource for the Account Statement view.
 * It aggregates account statement rows for multiple shareholders and formats them for export.
 */
@RequiredArgsConstructor
public class AccountStatementExportDataSource implements ExportDataSource {

    private final List<Shareholder> shareholders;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final AccountStatementService accountStatementService;
    private final MessageSource messageSource;
    private final Locale locale;

    @Override
    public String getSheetName(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("account-statements", null, locale);
    }

    @Override
    public List<String> getHeaders(MessageSource messageSource, Locale locale) {
        return List.of(
                messageSource.getMessage("bookings.shareholder", null, locale),
                messageSource.getMessage("account-statements.table.id", null, locale),
                messageSource.getMessage("account-statements.table.date", null, locale),
                messageSource.getMessage("account-statements.table.type", null, locale),
                messageSource.getMessage("account-statements.table.debit", null, locale),
                messageSource.getMessage("account-statements.table.credit", null, locale),
                messageSource.getMessage("account-statements.table.balance", null, locale)
        );
    }

    @Override
    public List<List<Object>> getRows() {
        List<ExportRow> rawRows = new ArrayList<>();

        for (Shareholder shareholder : shareholders) {
            final String shareholderName = shareholder.getFirstName() + " " + shareholder.getLastName();

            for (int yearValue = startDate.getYear(); yearValue <= endDate.getYear(); yearValue++) {
                Year year = Year.of(yearValue);
                RunningTotalAccountStatements statements = accountStatementService.runningTotalAccountStatements(
                        shareholder, year, messageSource, locale);

                for (RunningTotalAccountStatement stmt : statements.runningTotalAccountStatements()) {
                    if (isWithinRange(stmt.date())) {
                        BigDecimal amount = stmt.amount().toBigDecimal();
                        BigDecimal debit = amount.compareTo(BigDecimal.ZERO) < 0 ? amount.abs() : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                        BigDecimal credit = amount.compareTo(BigDecimal.ZERO) >= 0 ? amount : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

                        rawRows.add(new ExportRow(
                                shareholderName,
                                stmt.id(),
                                stmt.date(),
                                stmt.label(),
                                debit,
                                credit,
                                stmt.currentBalance().toBigDecimal()
                        ));
                    }
                }
            }
        }

        // Sort: Shareholder Name ASC, then Date ASC
        rawRows.sort(Comparator.comparing(ExportRow::shareholderName)
                .thenComparing(ExportRow::date));

        return rawRows.stream()
                .map(r -> {
                    List<Object> list = new ArrayList<>();
                    list.add(r.shareholderName());
                    list.add(r.id() != null ? r.id() : "");
                    list.add(r.date());
                    list.add(r.type());
                    list.add(r.debit());
                    list.add(r.credit());
                    list.add(r.balance());
                    return list;
                })
                .collect(Collectors.toList());
    }

    private boolean isWithinRange(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    private record ExportRow(
            String shareholderName,
            Integer id,
            LocalDate date,
            String type,
            BigDecimal debit,
            BigDecimal credit,
            BigDecimal balance
    ) {}
}
