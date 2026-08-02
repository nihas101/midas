package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.core.bookings.dto.Booking;
import de.nihas101.midas.core.bookings.entity.BookingType;
import de.nihas101.midas.core.bookings.monthlytotal.MonthlyTotal;
import de.nihas101.midas.core.money.MoneyAmount;

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
