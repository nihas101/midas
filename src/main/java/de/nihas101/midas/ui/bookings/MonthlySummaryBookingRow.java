package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.monthlytotal.MonthlySumTotalCalculator;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotalCalculator;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.Map;

@RequiredArgsConstructor
public class MonthlySummaryBookingRow implements BookingRow {

    private final BookingRow bookingRow;

    public MonthlySummaryBookingRow(
            final String comment,
            final Bookings bookings,
            final Month month
    ) {
        this(
                comment,
                new MonthlySumTotalCalculator(bookings, month)
        );
    }

    public MonthlySummaryBookingRow(
            final String comment,
            final MonthlyTotalCalculator monthlyTotalCalculator
    ) {
        final Map<BookingType, MoneyAmount> monthTotals = monthlyTotalCalculator.monthlyTotal();
        final MoneyAmount monthTotalSum = monthTotals.values()
                .stream()
                .reduce(MoneyAmount.ZERO, MoneyAmount::plus);

        this.bookingRow = new BaseBookingRow(
                "",
                "",
                comment,
                monthTotals,
                monthTotalSum,
                MoneyAmount.ZERO
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
