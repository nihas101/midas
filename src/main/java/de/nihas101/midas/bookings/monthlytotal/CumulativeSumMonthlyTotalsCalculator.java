package de.nihas101.midas.bookings.monthlytotal;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.MonthlyBookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
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
