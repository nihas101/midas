package de.nihas101.midas.ui.accountstatement;

import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatement;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.Month;
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
        final LocalDate date = accountStatement.year().atMonth(Month.DECEMBER).atEndOfMonth();
        return date.format(DateTimeFormatter.ofPattern("dd.MM")); // TODO: Make this configurable
    }

    @Override
    public BookingType bookingType() {
        return accountStatement.type();
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
}
