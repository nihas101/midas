package de.nihas101.midas.core.accountstatement.runningtotal;

import de.nihas101.midas.core.accountstatement.dto.LabeledAccountStatement;
import de.nihas101.midas.core.bookings.entity.BookingType;
import de.nihas101.midas.core.money.MoneyAmount;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class DefaultRunningTotalAccountStatement implements RunningTotalAccountStatement {

    private final LabeledAccountStatement statement;
    private final MoneyAmount currentBalance;

    @Override
    public Integer id() {
        return statement != null ? statement.id() : null;
    }

    @Override
    public LocalDate date() {
        return statement != null ? statement.date() : null;
    }

    @Override
    public String label() {
        return statement != null ? statement.label() : null;
    }

    @Override
    public MoneyAmount amount() {
        return statement != null ? statement.amount() : null;
    }

    @Override
    public MoneyAmount currentBalance() {
        return statement != null ? currentBalance : null;
    }

    @Override
    public boolean isHidden() {
        return statement != null && statement.isHidden();
    }

    @Override
    public boolean isManualExtra() {
        return statement != null && statement.isManualExtra();
    }

    @Override
    public BookingType bookingType() {
        return statement != null ? statement.bookingType() : null;
    }
}
