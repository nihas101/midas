package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;

import java.time.Month;

public interface Bookings {
    MoneyAmount openingBalance();

    // TODO: Wrap this return in a class
    MonthlyBookings bookingsInMonth(final Month month);
}
