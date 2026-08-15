package de.nihas101.midas.core.accountstatement.row;

import de.nihas101.midas.api.accountstatement.AccountStatementRow;
import de.nihas101.midas.api.accountstatement.RunningTotalAccountStatement;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.accountstatement.runningtotal.OpeningRunningTotalAccountStatement;
import de.nihas101.midas.core.config.AccountStatementConfig;
import io.micrometer.common.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RunningTotalAccountStatementRow implements AccountStatementRow {

    private final RunningTotalAccountStatement accountStatement;
    private final String dateFormat;

    public RunningTotalAccountStatementRow(final RunningTotalAccountStatement accountStatement) {
        this(accountStatement, AccountStatementConfig.DEFAULT_DATE_FORMAT);
    }

    public RunningTotalAccountStatementRow(
            final RunningTotalAccountStatement accountStatement,
            final String dateFormat
    ) {
        this.accountStatement = accountStatement;
        this.dateFormat = StringUtils.isNotBlank(dateFormat) ? dateFormat : AccountStatementConfig.DEFAULT_DATE_FORMAT;
    }

    @Override
    public Integer displayId() {
        return accountStatement.id();
    }

    @Override
    public String dateStr() {
        final LocalDate date = accountStatement.date();
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern(dateFormat));
        } else {
            return "";
        }
    }

    @Override
    public String label() {
        return accountStatement.label();
    }

    @Override
    public MoneyAmount debit() {
        final MoneyAmount amount = accountStatement.amount();
        return amount.smallerThan(MoneyAmount.ZERO) ? amount : null;
    }

    @Override
    public MoneyAmount credit() {
        final MoneyAmount amount = accountStatement.amount();
        return amount.smallerThan(MoneyAmount.ZERO) ? null : amount;
    }

    @Override
    public MoneyAmount balance() {
        return accountStatement.currentBalance();
    }

    @Override
    public MoneyAmount amount() {
        return accountStatement.amount();
    }

    @Override
    public boolean isOpeningBalance() {
        return accountStatement instanceof OpeningRunningTotalAccountStatement;
    }

    @Override
    public boolean isHidden() {
        return accountStatement.isHidden();
    }

    @Override
    public boolean isManualExtra() {
        return accountStatement.isManualExtra();
    }

    @Override
    public BookingType bookingType() {
        return accountStatement.bookingType();
    }

    @Override
    public String rowKey() {
        return accountStatement.rowKey();
    }
}
