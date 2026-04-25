package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.openingbalance.dto.OpeningBalance;

import java.time.Month;
import java.util.function.Function;

public interface Bookings {
    OpeningBalance openingBalance();

    FilteredBookings bookingsInMonth(final Month month);

    FilteredBookings filter(Function<Booking, Boolean> condition);

    boolean isEmpty();
}
