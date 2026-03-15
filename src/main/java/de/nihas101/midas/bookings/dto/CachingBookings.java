package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CachingBookings implements Bookings {

    private final Bookings delegate;
    private final Map<Month, MonthlyBookings> groupedByMonth;
    private MoneyAmount initialBalance;

    public CachingBookings(final Bookings delegate) {
        this(
                delegate,
                new HashMap<>()
        );
    }

    @Override
    public MoneyAmount openingBalance() {
        if (initialBalance == null) {
            initialBalance = delegate.openingBalance();
        }
        return initialBalance;
    }

    @Override
    public MonthlyBookings bookingsInMonth(Month month) {
        if (!groupedByMonth.containsKey(month)) {
            groupedByMonth.put(month, delegate.bookingsInMonth(month));
        }
        return groupedByMonth.get(month);
    }
}
