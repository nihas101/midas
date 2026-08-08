package de.nihas101.midas.api.bookings;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;

import java.util.Map;

public interface MonthlyTotalsCalculator {
    Map<BookingType, MoneyAmount> monthlyTotals();
}
