package de.nihas101.midas.core.bookings.monthlytotal;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;

public interface MonthlyTotal {
    MoneyAmount monthlyTotal(BookingType bookingType);
}
