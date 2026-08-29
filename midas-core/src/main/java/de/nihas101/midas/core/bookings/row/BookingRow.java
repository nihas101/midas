package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.MonthlyTotal;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;

import java.util.List;

public interface BookingRow {
    MoneyAmount amount(BookingType type);

    String displayId();

    String formattedDate();

    String comment();

    MonthlyTotal amounts();

    MoneyAmount total();

    MoneyAmount balance();

    List<Booking> bookings();

    default String partName() {
        return "no-separator-column";
    }
}
