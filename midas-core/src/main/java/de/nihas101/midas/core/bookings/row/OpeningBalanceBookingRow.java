package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.core.bookings.dto.Booking;
import de.nihas101.midas.core.bookings.dto.Bookings;
import de.nihas101.midas.core.bookings.entity.BookingType;
import de.nihas101.midas.core.bookings.monthlytotal.MonthlyTotal;
import de.nihas101.midas.core.bookings.monthlytotal.MonthlyTotalSum;
import de.nihas101.midas.core.money.MoneyAmount;

import java.util.Collections;
import java.util.List;

public record OpeningBalanceBookingRow(
        MoneyAmount balance,
        List<Booking> bookings
) implements BookingRow { // TODO: Test

    public OpeningBalanceBookingRow(final Bookings bookings) {
        this(
                bookings.openingBalance().getOpeningBalance(),
                Collections.emptyList()
        );
    }

    @Override
    public MoneyAmount amount(final BookingType type) {
        return MonthlyTotalSum.ZERO.monthlyTotal(type);
    }

    @Override
    public String displayId() {
        return "";
    }

    @Override
    public String dateStr() {
        return "01.01.";
    }

    @Override
    public String comment() {
        return "";
    }

    @Override
    public MonthlyTotal amounts() {
        return MonthlyTotalSum.ZERO;
    }

    @Override
    public MoneyAmount total() {
        return MoneyAmount.ZERO;
    }

}
