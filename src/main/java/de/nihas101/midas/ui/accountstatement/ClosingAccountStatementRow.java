package de.nihas101.midas.ui.accountstatement;

import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

// TODO: Separate this from AccountStatementRow
@RequiredArgsConstructor
public class ClosingAccountStatementRow implements AccountStatementRow {
    private final MoneyAmount closingBalance;
    private final String label;

    public ClosingAccountStatementRow(
            final RunningTotalAccountStatements accountStatements,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                accountStatements.runningTotalAccountStatements().getLast().currentBalance(),
                messageSource.getMessage(
                        "account-statement.final-balance",
                        new Object[]{
                                accountStatements.runningTotalAccountStatements()
                                        .getLast().date()
                                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        },
                        locale
                )
        );
    }

    @Override
    public Integer displayId() {
        return 0;
    }

    @Override
    public String dateStr() {
        return "";
    }

    @Override
    public String label() {
        return label;
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

    @Override
    public String partName() {
        return "double-separator";
    }
}
