package de.nihas101.midas.core.accountstatement.row;

import de.nihas101.midas.api.accountstatement.AccountStatementRow;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatements;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.config.DatesConfig;
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
            final DatesConfig datesConfig,
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
                                        .format(DateTimeFormatter.ofPattern(datesConfig.getLongDateFormat()))
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
    public String formattedDate() {
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
