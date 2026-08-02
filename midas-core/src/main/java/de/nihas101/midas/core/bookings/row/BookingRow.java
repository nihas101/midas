package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingType;
import de.nihas101.midas.api.money.MoneyAmount;
import de.nihas101.midas.core.bookings.monthlytotal.MonthlyTotal;

import java.util.List;

public interface BookingRow {
    MoneyAmount amount(BookingType type);

    String displayId();

    String dateStr(); // TODO: Think of a better name dateStr is not good

    String comment();

    MonthlyTotal amounts();

    MoneyAmount total();

    MoneyAmount balance();

    List<Booking> bookings();

    default String partName() {
        return "no-separator-column";
    }
}
