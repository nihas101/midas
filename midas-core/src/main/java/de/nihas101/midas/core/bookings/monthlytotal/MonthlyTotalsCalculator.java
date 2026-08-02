package de.nihas101.midas.core.bookings.monthlytotal;

import de.nihas101.midas.api.bookings.BookingType;
import de.nihas101.midas.api.money.MoneyAmount;

import java.util.Map;

public interface MonthlyTotalsCalculator {
    Map<BookingType, MoneyAmount> monthlyTotals();
}
