package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.MonthlyTotal;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.monthlytotal.DefaultMonthlyTotalSum;

import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Collections.emptyList;

public record OpeningBalanceBookingRow(
        MoneyAmount balance,
        List<Booking> bookings,
        String dateFormat,
        Year year
) implements BookingRow {

    public OpeningBalanceBookingRow(
            final Bookings bookings,
            final String dateFormat,
            final Year year
    ) {
        this(
                bookings.openingBalance().getOpeningBalance(),
                emptyList(),
                dateFormat,
                year
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
    public String formattedDate() {
        return year.atMonth(Month.JANUARY).atDay(1).format(DateTimeFormatter.ofPattern(dateFormat));
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
