package de.nihas101.midas.core.bookings.dto;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.FilteredBookings;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultBookingsTest {

    @Test
    void openingBalance_sumsOnlySaldovortrag() {
        List<Booking> bookings = List.of(
                createBooking(BookingType.WITHDRAWAL, 2000L, Month.JANUARY)
        );
        Bookings defaultBookings = new DefaultBookings(bookings, new DefaultOpeningBalance(1, 2, MoneyAmount.ofCents(1500L), Year.of(2026), Source.USER));
        assertEquals(MoneyAmount.ofCents(1500L), defaultBookings.openingBalance().getOpeningBalance());
    }

    @Test
    void openingBalance_emptyReturnsZero() {
        Bookings defaultBookings = new DefaultBookings(List.of(), null);
        assertEquals(MoneyAmount.ZERO, defaultBookings.openingBalance().getOpeningBalance());
    }

    @ParameterizedTest
    @EnumSource(Month.class)
    void bookingsInMonth_filtersByMonth(Month month) {
        Booking correctMonth = createBooking(BookingType.WITHDRAWAL, 1000L, month);
        Month otherMonth = (month == Month.DECEMBER) ? Month.JANUARY : month.plus(1);
        Booking wrongMonth = createBooking(BookingType.WITHDRAWAL, 2000L, otherMonth);

        Bookings defaultBookings = new DefaultBookings(List.of(correctMonth, wrongMonth), new DefaultOpeningBalance(1, 2, MoneyAmount.ofCents(5000L), Year.of(2026), Source.USER));

        FilteredBookings result = defaultBookings.bookingsInMonth(month);

        assertEquals(1, result.bookings().size(), "Should only contain one booking for " + month);
        assertTrue(result.bookings().contains(correctMonth));
        assertFalse(result.bookings().contains(wrongMonth));
    }

    private Booking createBooking(final BookingType type, final long cents, final Month month) {
        return DefaultBooking.builder()
                .type(type)
                .amount(MoneyAmount.ofCents(cents))
                .date(LocalDate.of(2026, month, 1))
                .build();
    }
}
