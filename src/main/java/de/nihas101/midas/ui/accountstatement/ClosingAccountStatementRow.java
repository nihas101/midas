package de.nihas101.midas.ui.accountstatement;

import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatement;
import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// TODO: Separate this from AccountStatementRow
@RequiredArgsConstructor
public class ClosingAccountStatementRow implements AccountStatementRow {
    private final MoneyAmount closingBalance;
    private final LocalDate date;

    public ClosingAccountStatementRow(final RunningTotalAccountStatements accountStatements) {
        this(
                accountStatements.runningTotalAccountStatements().getLast()
        );
    }

    private ClosingAccountStatementRow(final RunningTotalAccountStatement runningTotalAccountStatement) {
        this(
                runningTotalAccountStatement.currentBalance(),
                runningTotalAccountStatement.date()
        );
    }

    @Override
    public Integer displayId() {
        return 0;
    }

    @Override
    public String dateStr() {
        return "Endstand per " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")); // TODO: i18n + extract pattern
    }

    @Override
    public String label(final MessageSource messageSource, final Locale locale) {
        return "";
    }

    @Override
    public MoneyAmount debit() {
        return null;
    }

    @Override
    public MoneyAmount credit() {
        return null;
    }

    @Override
    public MoneyAmount balance() {
        return closingBalance;
    }
}
