package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;

public interface AccountStatements {
    OpeningBalance openingBalance();

    LabeledAccountStatement forType(BookingType bookingType);
}
