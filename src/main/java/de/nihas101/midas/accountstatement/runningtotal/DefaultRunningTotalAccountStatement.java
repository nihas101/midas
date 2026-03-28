package de.nihas101.midas.accountstatement.runningtotal;

import de.nihas101.midas.accountstatement.dto.AccountStatement;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.time.Year;

@RequiredArgsConstructor
public class DefaultRunningTotalAccountStatement implements RunningTotalAccountStatement {

    private final AccountStatement statement;
    private final MoneyAmount currentBalance;

    @Override
    public Integer id() {
        return statement.id();
    }

    @Override
    public Year year() {
        return statement.year();
    }

    @Override
    public BookingType type() {
        return statement.type();
    }

    @Override
    public MoneyAmount amount() {
        return statement.amount();
    }

    @Override
    public MoneyAmount currentBalance() {
        return currentBalance;
    }
}
