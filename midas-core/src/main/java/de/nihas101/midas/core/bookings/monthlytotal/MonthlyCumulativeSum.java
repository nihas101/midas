package de.nihas101.midas.core.bookings.monthlytotal;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Month;
import java.util.Map;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class MonthlyCumulativeSum implements MonthlyTotal, Sum {

    private final Map<BookingType, MoneyAmount> monthlyTotals;

    public MonthlyCumulativeSum(
            final Bookings bookings,
            final Month month
    ) {
        this(new CumulativeSumMonthlyTotalsCalculator(bookings, month));
    }

    public MonthlyCumulativeSum(final MonthlyTotalsCalculator monthlyTotalsCalculator) {
        this(monthlyTotalsCalculator != null ? monthlyTotalsCalculator.monthlyTotals() : null);
    }

    @Override
    public MoneyAmount monthlyTotal(BookingType bookingType) {
        if (monthlyTotals == null) {
            return MoneyAmount.ZERO;
        }
        return monthlyTotals.getOrDefault(bookingType, MoneyAmount.ZERO);
    }

    @Override
    public MoneyAmount sum() {
        if (monthlyTotals == null) {
            return MoneyAmount.ZERO;
        }
        return monthlyTotals.values()
                .stream()
                .reduce(MoneyAmount.ZERO, MoneyAmount::plus);
    }

}
