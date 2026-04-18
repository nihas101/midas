package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.openingbalance.dto.OpeningBalance;

import java.time.Month;
import java.util.List;

public interface Bookings {
    OpeningBalance openingBalance();

    MonthlyBookings bookingsInMonth(final Month month);

    List<Booking> allBookings(); // TODO: This can be done better (at least don't expose them via list)

    boolean isEmpty();
}
