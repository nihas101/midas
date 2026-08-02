package de.nihas101.midas.core.bookings.dto;

import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.FilteredBookings;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class DefaultBookings implements Bookings {
    private final List<de.nihas101.midas.api.bookings.Booking> bookings;
    private final OpeningBalance openingBalance;

    @Override
    public OpeningBalance openingBalance() {
        if (openingBalance == null) {
            return new DefaultOpeningBalance(MoneyAmount.ZERO);
        }
        return openingBalance;
    }

    @Override
    public FilteredBookings bookingsInMonth(final Month month) {
        return filter(b -> month.equals(b.getDate().getMonth()));
    }

    @Override
    public FilteredBookings filter(final Function<de.nihas101.midas.api.bookings.Booking, Boolean> condition) {
        final List<de.nihas101.midas.api.bookings.Booking> filteredBookings = bookings.stream()
                .filter(condition::apply)
                .toList();
        return new FilteredBookings(filteredBookings);
    }

    @Override
    public boolean isEmpty() {
        return bookings.isEmpty() && openingBalance == null;
    }
}
