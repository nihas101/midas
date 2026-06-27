package de.nihas101.midas.accountstatement.row;

import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatement;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class RunningTotalAccountStatementRow implements AccountStatementRow {

    private final RunningTotalAccountStatement accountStatement;

    @Override
    public Integer displayId() {
        return accountStatement.id();
    }

    @Override
    public String dateStr() {
        return accountStatement.date().format(DateTimeFormatter.ofPattern("dd.MM")); // TODO: Make this configurable
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
        return accountStatement instanceof de.nihas101.midas.accountstatement.runningtotal.OpeningRunningTotalAccountStatement;
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
    public de.nihas101.midas.bookings.entity.BookingType bookingType() {
        return accountStatement.bookingType();
    }
}
