package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;

import java.util.List;

public interface AccountStatements {
    OpeningBalance openingBalance();

    LabeledAccountStatement forType(BookingType bookingType);

    default List<LabeledAccountStatement> manualStatements() {
        return List.of();
    }
}
