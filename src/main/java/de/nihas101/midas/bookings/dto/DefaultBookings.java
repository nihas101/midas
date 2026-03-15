package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.List;

@RequiredArgsConstructor
public class DefaultBookings implements Bookings {
    private final List<Booking> bookings;
    private final OpeningBalance openingBalance;

    @Override
    public MoneyAmount openingBalance() {
        if (openingBalance == null) {
            return MoneyAmount.ZERO;
        }
        return openingBalance.getOpeningBalance(); // TODO: Return the opening balance instead!
    }

    @Override
    public MonthlyBookings bookingsInMonth(final Month month) {
        final List<Booking> monthlyBookings = bookings.stream()
                .filter(b -> month.equals(b.getDate().getMonth()))
                .toList();
        return new MonthlyBookings(monthlyBookings);
    }
}
