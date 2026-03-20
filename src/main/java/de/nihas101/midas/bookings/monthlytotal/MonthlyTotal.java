package de.nihas101.midas.bookings.monthlytotal;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;

public interface MonthlyTotal {
    MoneyAmount monthlyTotal(BookingType bookingType);
}
