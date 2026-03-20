package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.List;

@RequiredArgsConstructor
public class DefaultBookings implements Bookings {
    private final List<Booking> bookings;
    private final OpeningBalance openingBalance;

    @Override
    public OpeningBalance openingBalance() {
        if (openingBalance == null) {
            return new OpeningBalance(MoneyAmount.ZERO);
        }
        return openingBalance;
    }

    @Override
    public MonthlyBookings bookingsInMonth(final Month month) {
        final List<Booking> monthlyBookings = bookings.stream()
                .filter(b -> month.equals(b.getDate().getMonth()))
                .toList();
        return new MonthlyBookings(monthlyBookings);
    }
}
