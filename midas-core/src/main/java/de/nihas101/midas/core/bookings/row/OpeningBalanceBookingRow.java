package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.MonthlyTotal;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.monthlytotal.DefaultMonthlyTotalSum;

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
        return DefaultMonthlyTotalSum.ZERO.monthlyTotal(type);
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
        return DefaultMonthlyTotalSum.ZERO;
    }

    @Override
    public MoneyAmount total() {
        return MoneyAmount.ZERO;
    }

}
