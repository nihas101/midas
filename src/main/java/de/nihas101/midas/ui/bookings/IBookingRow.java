package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;

import java.util.Map;

// TODO: Break this interface down?
public interface IBookingRow {
    MoneyAmount amount(BookingType type);

    String displayId();

    String dateStr();

    String comment();

    Map<BookingType, MoneyAmount> amounts();

    MoneyAmount total();

    MoneyAmount balance();
}
