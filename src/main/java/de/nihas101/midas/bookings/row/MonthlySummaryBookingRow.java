package de.nihas101.midas.bookings.row;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotal;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotalSum;
import de.nihas101.midas.money.MoneyAmount;

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
                new MonthlyTotalSum(bookings, month)
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
    public String dateStr() {
        return "";
    }

}
