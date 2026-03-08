package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CachingBookings implements Bookings {

    private final Bookings delegate;
    private final Map<Month, List<Booking>> groupedByMonth;
    private MoneyAmount initialBalance;

    public CachingBookings(final Bookings delegate) {
        this(
                delegate,
                new HashMap<>()
        );
    }

    @Override
    public MoneyAmount initialBalance() {
        if (initialBalance == null) {
            initialBalance = delegate.initialBalance();
        }
        return initialBalance;
    }

    @Override
    public List<Booking> groupBookingsByMonth(Month month) {
        if (!groupedByMonth.containsKey(month)) {
            groupedByMonth.put(month, delegate.groupBookingsByMonth(month));
        }
        return groupedByMonth.get(month);
    }
}
