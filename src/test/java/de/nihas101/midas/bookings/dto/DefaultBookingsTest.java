package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
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
        DefaultBookings defaultBookings = new DefaultBookings(bookings, new OpeningBalance(1, 2, MoneyAmount.ofCents(1500L), Year.of(2026)));
        assertEquals(MoneyAmount.ofCents(1500L), defaultBookings.openingBalance().getOpeningBalance());
    }

    @Test
    void openingBalance_emptyReturnsZero() {
        DefaultBookings defaultBookings = new DefaultBookings(List.of(), null);
        assertEquals(MoneyAmount.ZERO, defaultBookings.openingBalance().getOpeningBalance());
    }

    @ParameterizedTest
    @EnumSource(Month.class)
    void bookingsInMonth_filtersByMonth(Month month) {
        Booking correctMonth = createBooking(BookingType.WITHDRAWAL, 1000L, month);
        Month otherMonth = (month == Month.DECEMBER) ? Month.JANUARY : month.plus(1);
        Booking wrongMonth = createBooking(BookingType.WITHDRAWAL, 2000L, otherMonth);

        DefaultBookings defaultBookings = new DefaultBookings(List.of(correctMonth, wrongMonth), new OpeningBalance(1, 2, MoneyAmount.ofCents(5000L), Year.of(2026)));

        MonthlyBookings result = defaultBookings.bookingsInMonth(month);

        assertEquals(1, result.bookings().size(), "Should only contain one booking for " + month);
        assertTrue(result.bookings().contains(correctMonth));
        assertFalse(result.bookings().contains(wrongMonth));
    }

    private Booking createBooking(BookingType type, long cents, Month month) {
        return Booking.builder()
                .type(type)
                .amount(MoneyAmount.ofCents(cents))
                .date(LocalDate.of(2026, month, 1))
                .build();
    }
}
