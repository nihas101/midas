package de.nihas101.midas.bookings.row;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.monthlytotal.MonthlyCumulativeSum;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotal;
import de.nihas101.midas.money.MoneyAmount;
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
                monthlyCumulativeSum.sum(), // TODO: Sum is calculated twice here. Think on how to avoid that
                bookings.openingBalance()
                        .getOpeningBalance()
                        .plus(monthlyCumulativeSum.sum()),
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
