package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;

import java.util.Map;

// TODO: Break this interface down? (or at least rename it to something more clear -> Used to carry info about booking that will be displayed in a row)
public interface BookingRow {
    MoneyAmount amount(BookingType type);

    String displayId();

    String dateStr();

    String comment();

    Map<BookingType, MoneyAmount> amounts();

    MoneyAmount total();

    MoneyAmount balance();
}
