package de.nihas101.midas.core.accountstatement.dto;

import de.nihas101.midas.core.bookings.entity.BookingType;
import de.nihas101.midas.core.openingbalance.dto.OpeningBalance;

import java.util.List;

public interface AccountStatements {
    OpeningBalance openingBalance();

    LabeledAccountStatement forType(BookingType bookingType);

    default List<LabeledAccountStatement> manualStatements() {
        return List.of();
    }
}
