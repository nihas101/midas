package de.nihas101.midas.bookings.monthlytotal;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.MonthlyBookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Month;
import java.util.Arrays;
import java.util.EnumMap;
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
        this(new CumulativeSumMonthlyTotalCalculator(bookings, month));
    }

    public MonthlyCumulativeSum(final CumulativeSumMonthlyTotalCalculator cumulativeSumMonthlyTotalCalculator) {
        this(cumulativeSumMonthlyTotalCalculator.monthlyTotals());
    }

    @Override
    public MoneyAmount monthlyTotal(BookingType bookingType) {
        return monthlyTotals.getOrDefault(bookingType, MoneyAmount.ZERO);
    }

    @Override
    public MoneyAmount sum() {
        return monthlyTotals.values()
                .stream()
                .reduce(MoneyAmount.ZERO, MoneyAmount::plus);
    }

    @RequiredArgsConstructor
    public static class CumulativeSumMonthlyTotalCalculator {

        private final Bookings bookings;
        private final Month month;

        public Map<BookingType, MoneyAmount> monthlyTotals() {
            final Map<BookingType, MoneyAmount> cumulativeTotals = new EnumMap<>(BookingType.class);
            Arrays.stream(BookingType.values()).forEach(t -> cumulativeTotals.put(t, MoneyAmount.ZERO));

            for (Month m : Month.values()) {
                final MonthlyBookings monthBookings = bookings.bookingsInMonth(m);
                for (Booking b : monthBookings.bookings()) {
                    cumulativeTotals.put(b.getType(), cumulativeTotals.get(b.getType()).plus(b.getAmount()));
                }
                if (m == month) {
                    break;
                }
            }
            return cumulativeTotals;
        }
    }
}
