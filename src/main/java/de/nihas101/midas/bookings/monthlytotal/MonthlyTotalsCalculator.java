package de.nihas101.midas.bookings.monthlytotal;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;

import java.util.Map;

public interface MonthlyTotalsCalculator {
    Map<BookingType, MoneyAmount> monthlyTotals();
}
