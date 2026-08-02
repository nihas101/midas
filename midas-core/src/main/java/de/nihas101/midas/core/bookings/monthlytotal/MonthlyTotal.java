package de.nihas101.midas.core.bookings.monthlytotal;

import de.nihas101.midas.api.bookings.BookingType;
import de.nihas101.midas.api.money.MoneyAmount;

public interface MonthlyTotal {
    MoneyAmount monthlyTotal(BookingType bookingType);
}
