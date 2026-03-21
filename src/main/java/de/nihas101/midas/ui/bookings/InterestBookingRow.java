package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotal;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class InterestBookingRow implements BookingRow {
    private final LocalDate localDate;
    private final MoneyAmount interest;

    @Override
    public MoneyAmount amount(final BookingType type) {
        return interest;
    }

    @Override
    public String displayId() {
        return "";
    }

    @Override
    public String dateStr() {
        return localDate.format(DateTimeFormatter.ofPattern("dd.MM")); // TODO: Make this pattern configurable
    }

    @Override
    public String comment() {
        return "";
    }

    @Override
    public MonthlyTotal amounts() {
        return null;
    }

    @Override
    public MoneyAmount total() {
        return null;
    }

    @Override
    public MoneyAmount balance() {
        return null;
    }

    @Override
    public List<Booking> bookings() {
        return List.of();
    }
}
