package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultBookingsTest {

    @Test
    void initialBalance_sumsOnlySaldovortrag() {
        List<Booking> bookings = List.of(
                createBooking(BookingType.OPENING_BALANCE, 1000L, Month.JANUARY),
                createBooking(BookingType.OPENING_BALANCE, 500L, Month.JANUARY),
                createBooking(BookingType.WITHDRAWAL, 2000L, Month.JANUARY)
        );
        DefaultBookings defaultBookings = new DefaultBookings(bookings);
        assertEquals(MoneyAmount.ofCents(1500L), defaultBookings.initialBalance());
    }

    @Test
    void initialBalance_emptyReturnsZero() {
        DefaultBookings defaultBookings = new DefaultBookings(List.of());
        assertEquals(MoneyAmount.ZERO, defaultBookings.initialBalance());
    }

    @ParameterizedTest
    @EnumSource(Month.class)
    void bookingsInMonth_filtersByMonthAndIgnoresSaldovortrag(Month month) {
        Booking correctMonth = createBooking(BookingType.WITHDRAWAL, 1000L, month);
        Month otherMonth = (month == Month.DECEMBER) ? Month.JANUARY : month.plus(1);
        Booking wrongMonth = createBooking(BookingType.WITHDRAWAL, 2000L, otherMonth);
        Booking saldovortragInMonth = createBooking(BookingType.OPENING_BALANCE, 5000L, month);

        DefaultBookings defaultBookings = new DefaultBookings(List.of(correctMonth, wrongMonth, saldovortragInMonth));

        MonthlyBookings result = defaultBookings.bookingsInMonth(month);

        assertEquals(1, result.bookings().size(), "Should only contain one booking for " + month);
        assertTrue(result.bookings().contains(correctMonth));
        assertFalse(result.bookings().contains(wrongMonth));
        assertFalse(result.bookings().contains(saldovortragInMonth));
    }

    private Booking createBooking(BookingType type, long cents, Month month) {
        return Booking.builder()
                .type(type)
                .amount(MoneyAmount.ofCents(cents))
                .date(LocalDate.of(2026, month, 1))
                .build();
    }
}
