package de.nihas101.midas.api.accountstatement;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.api.openingbalance.OpeningBalance;

import java.util.List;

public interface AccountStatements {
    OpeningBalance openingBalance();

    LabeledAccountStatement forType(BookingType bookingType);

    default List<LabeledAccountStatement> manualStatements() {
        return List.of();
    }
}
