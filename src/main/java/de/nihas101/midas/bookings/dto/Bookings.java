package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;

import java.time.Month;
import java.util.List;

public interface Bookings {
    MoneyAmount initialBalance();

    // TODO: Wrap this return in a class
    List<Booking> groupBookingsByMonth(final Month month);
}
