package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;

import java.time.Year;

public interface AccountStatement {
    Integer id();

    Year year();

    BookingType type();

    MoneyAmount amount();
}
