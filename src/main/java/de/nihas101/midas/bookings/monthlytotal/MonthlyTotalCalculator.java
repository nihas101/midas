package de.nihas101.midas.bookings.monthlytotal;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;

import java.util.Map;

public interface MonthlyTotalCalculator { // TODO: This and its impls could be in the common package perhaps

    // TODO: Wrap the return value
    Map<BookingType, MoneyAmount> monthlyTotal();
}
