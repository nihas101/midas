package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.monthlytotal.CumulativeSumMonthlyTotalCalculator;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotalCalculator;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.Map;

@RequiredArgsConstructor
public class CumulativeSummaryBookingRow implements BookingRow {

    private final BookingRow bookingRow;

    public CumulativeSummaryBookingRow(
            final String dateStr,
            final String comment,
            final Bookings bookings,
            final Month month
    ) {
        this(
                dateStr,
                comment,
                bookings,
                new CumulativeSumMonthlyTotalCalculator(bookings, month)
        );
    }

    public CumulativeSummaryBookingRow(
            final String dateStr,
            final String comment,
            final Bookings bookings,
            final MonthlyTotalCalculator cumulativeTotalCalculator
    ) {
        final Map<BookingType, MoneyAmount> cumulativeTotals = cumulativeTotalCalculator.monthlyTotal();
        final MoneyAmount totalSumOfAllBookings = cumulativeTotals.values().stream()
                .reduce(MoneyAmount.ZERO, MoneyAmount::plus);
        final MoneyAmount balance = bookings.openingBalance().plus(totalSumOfAllBookings);

        this.bookingRow = new BaseBookingRow(
                "",
                dateStr,
                comment,
                cumulativeTotals,
                totalSumOfAllBookings,
                balance
        );
    }

    @Override
    public MoneyAmount amount(final BookingType type) {
        return bookingRow.amount(type);
    }

    @Override
    public String displayId() {
        return bookingRow.displayId();
    }

    @Override
    public String dateStr() {
        return bookingRow.dateStr();
    }

    @Override
    public String comment() {
        return bookingRow.comment();
    }

    @Override
    public Map<BookingType, MoneyAmount> amounts() {
        return bookingRow.amounts();
    }

    @Override
    public MoneyAmount total() {
        return bookingRow.total();
    }

    @Override
    public MoneyAmount balance() {
        return bookingRow.balance();
    }

    @Override
    public String partName() {
        return "month-separator"; // TODO: This is used to add top and bottom separators
    }
}
