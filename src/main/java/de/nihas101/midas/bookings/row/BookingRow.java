package de.nihas101.midas.bookings.row;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotal;
import de.nihas101.midas.money.MoneyAmount;

import java.util.List;

// TODO: Break this interface down? (or at least rename it to something more clear -> Used to carry info about booking that will be displayed in a row)
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
