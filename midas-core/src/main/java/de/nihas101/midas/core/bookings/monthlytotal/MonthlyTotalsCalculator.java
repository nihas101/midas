package de.nihas101.midas.core.bookings.monthlytotal;

import de.nihas101.midas.core.bookings.entity.BookingType;
import de.nihas101.midas.core.money.MoneyAmount;

import java.util.Map;

public interface MonthlyTotalsCalculator {
    Map<BookingType, MoneyAmount> monthlyTotals();
}
