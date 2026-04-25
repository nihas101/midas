package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.List;
import java.util.function.Function;

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
    public FilteredBookings bookingsInMonth(final Month month) {
        return filter(b -> month.equals(b.getDate().getMonth()));
    }

    @Override
    public FilteredBookings filter(final Function<Booking, Boolean> condition) {
        final List<Booking> filteredBookings = bookings.stream()
                .filter(condition::apply)
                .toList();
        return new FilteredBookings(filteredBookings);
    }

    @Override
    public boolean isEmpty() {
        return bookings.isEmpty() && openingBalance == null;
    }
}
