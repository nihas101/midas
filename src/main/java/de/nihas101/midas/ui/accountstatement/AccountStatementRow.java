package de.nihas101.midas.ui.accountstatement;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;

public interface AccountStatementRow {
    Integer displayId();

    String dateStr();

    BookingType bookingType();

    MoneyAmount debit();

    MoneyAmount credit();

    MoneyAmount balance();
}
