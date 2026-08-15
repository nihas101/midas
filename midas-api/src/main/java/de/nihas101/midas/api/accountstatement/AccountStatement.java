package de.nihas101.midas.api.accountstatement;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;

import java.time.LocalDate;

public interface AccountStatement {
    Integer id();

    default BookingType type() {
        return null;
    }

    LocalDate date();

    MoneyAmount amount();
}
