package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.monthlytotal.DefaultMonthlyTotalCalculator;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotalCalculator;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.Map;

@RequiredArgsConstructor
public class MonthlySummaryBookingRow implements IBookingRow {

    private final BookingRow bookingRow;

    public MonthlySummaryBookingRow(
            final String dateStr,
            final String comment,
            final Bookings bookings,
            final Month month
    ) {
        this(
                dateStr,
                comment,
                bookings,
                new DefaultMonthlyTotalCalculator(bookings, month)
        );
    }

    public MonthlySummaryBookingRow(
            final String dateStr,
            final String comment,
            final Bookings bookings,
            final MonthlyTotalCalculator monthlyTotalCalculator
    ) {
        this(
                new BookingRow(
                        "",
                        dateStr,
                        comment,
                        monthlyTotalCalculator.monthlyTotal(),
                        monthlyTotalCalculator.monthlyTotal()
                                .values()
                                .stream()
                                .reduce(MoneyAmount.ZERO, MoneyAmount::plus),
                        bookings.initialBalance()
                )
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
}
