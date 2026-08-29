package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.MonthlyTotal;
import de.nihas101.midas.api.bookings.MonthlyTotalSum;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.monthlytotal.DefaultMonthlyTotalSum;

import java.time.Month;
import java.util.Collections;
import java.util.List;

public record MonthlySummaryBookingRow(
        String comment,
        MonthlyTotal amounts,
        MoneyAmount total,
        MoneyAmount balance,
        List<Booking> bookings
) implements BookingRow {

    public MonthlySummaryBookingRow(
            final String comment,
            final Bookings bookings,
            final Month month
    ) {
        this(
                comment,
                new DefaultMonthlyTotalSum(bookings, month)
        );
    }

    public MonthlySummaryBookingRow(
            final String comment,
            final MonthlyTotalSum monthlyTotalSum
    ) {

        this(
                comment,
                monthlyTotalSum,
                monthlyTotalSum.sum(),
                MoneyAmount.ZERO,
                Collections.emptyList()
        );
    }

    @Override
    public MoneyAmount amount(final BookingType type) {
        return amounts.monthlyTotal(type);
    }

    @Override
    public String displayId() {
        return "";
    }

    @Override
    public String formattedDate() {
        return "";
    }

}
