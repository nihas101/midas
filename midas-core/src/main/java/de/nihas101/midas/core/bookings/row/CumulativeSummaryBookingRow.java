package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.MonthlyTotal;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.monthlytotal.MonthlyCumulativeSum;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class CumulativeSummaryBookingRow implements BookingRow {

    private final String dateStr;
    private final String comment;
    private final MonthlyCumulativeSum amounts;
    private final MoneyAmount total;
    private final MoneyAmount balance;
    private final List<Booking> bookings;
    private final String partName;

    public CumulativeSummaryBookingRow(
            final String dateStr,
            final String comment,
            final Bookings bookings,
            final Month month,
            final String partName
    ) {
        this(
                dateStr,
                comment,
                bookings,
                new MonthlyCumulativeSum(bookings, month),
                partName
        );
    }

    public CumulativeSummaryBookingRow(
            final String dateStr,
            final String comment,
            final Bookings bookings,
            final MonthlyCumulativeSum monthlyCumulativeSum,
            final String partName
    ) {
        this(
                dateStr,
                comment,
                monthlyCumulativeSum,
                monthlyCumulativeSum.sum(),
                bookings.openingBalance().getOpeningBalance(),
                partName
        );
    }

    private CumulativeSummaryBookingRow(
            final String dateStr,
            final String comment,
            final MonthlyCumulativeSum monthlyCumulativeSum,
            final MoneyAmount sum,
            final MoneyAmount openingBalance,
            final String partName
    ) {
        this(
                dateStr,
                comment,
                monthlyCumulativeSum,
                sum,
                openingBalance.plus(sum),
                Collections.emptyList(),
                partName
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
        return dateStr;
    }

    @Override
    public String comment() {
        return comment;
    }

    @Override
    public MonthlyTotal amounts() {
        return amounts;
    }

    @Override
    public MoneyAmount total() {
        return total;
    }

    @Override
    public MoneyAmount balance() {
        return balance;
    }

    @Override
    public List<Booking> bookings() {
        return bookings;
    }

    @Override
    public String partName() {
        return partName;
    }
}
