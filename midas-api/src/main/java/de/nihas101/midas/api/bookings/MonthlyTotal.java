package de.nihas101.midas.api.bookings;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;

public interface MonthlyTotal {
    MoneyAmount monthlyTotal(BookingType bookingType);
}
