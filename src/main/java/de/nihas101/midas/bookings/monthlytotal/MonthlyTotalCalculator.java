package de.nihas101.midas.bookings.monthlytotal;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;

import java.util.Map;

public interface MonthlyTotalCalculator { // TODO: This and its impls could be in the common package perhaps

    // TODO: Wrap the return value
    Map<BookingType, MoneyAmount> monthlyTotal();
}
