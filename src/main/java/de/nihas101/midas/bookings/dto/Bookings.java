package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.openingbalance.dto.OpeningBalance;

import java.time.Month;

public interface Bookings {
    OpeningBalance openingBalance();

    MonthlyBookings bookingsInMonth(final Month month);

    boolean isEmpty();
}
