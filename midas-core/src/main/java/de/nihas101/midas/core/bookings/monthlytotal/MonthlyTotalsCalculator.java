package de.nihas101.midas.core.bookings.monthlytotal;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;

import java.util.Map;

public interface MonthlyTotalsCalculator {
    Map<BookingType, MoneyAmount> monthlyTotals();
}
