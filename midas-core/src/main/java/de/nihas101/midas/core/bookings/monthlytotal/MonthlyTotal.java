package de.nihas101.midas.core.bookings.monthlytotal;

import de.nihas101.midas.core.bookings.entity.BookingType;
import de.nihas101.midas.core.money.MoneyAmount;

public interface MonthlyTotal {
    MoneyAmount monthlyTotal(BookingType bookingType);
}
