package de.nihas101.midas.core.bookings.monthlytotal;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingType;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.FilteredBookings;
import de.nihas101.midas.api.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

@RequiredArgsConstructor
public class CumulativeSumMonthlyTotalsCalculator implements MonthlyTotalsCalculator {

    private final Bookings bookings;
    private final Month month;

    @Override
    public Map<BookingType, MoneyAmount> monthlyTotals() {
        final Map<BookingType, MoneyAmount> cumulativeTotals = new EnumMap<>(BookingType.class);
        Arrays.stream(BookingType.values()).forEach(t -> cumulativeTotals.put(t, MoneyAmount.ZERO));
        if (bookings == null) {
            return cumulativeTotals;
        }

        for (Month m : Month.values()) {
            final FilteredBookings monthBookings = bookings.bookingsInMonth(m);
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
