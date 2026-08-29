package de.nihas101.midas.core.bookings.row;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.FilteredBookings;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.dto.DefaultBooking;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultBookingsToBookingRowConverterTest {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM");
    private static final MoneyAmount STARTING_BALANCE = MoneyAmount.ofCents(1000L);

    @Test
    void generate_withSingleBooking_producesOneRow() {
        final Booking booking = DefaultBooking.builder()
                .id(1)
                .displayId(1)
                .date(LocalDate.of(2025, 3, 15))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmount.ofCents(-500L))
                .comment("Test withdrawal")
                .build();

        final FilteredBookings filteredBookings = new FilteredBookings(List.of(booking));
        final List<BookingRow> captured = new ArrayList<>();

        final DefaultBookingsToBookingRowConverter converter = new DefaultBookingsToBookingRowConverter(
                filteredBookings,
                STARTING_BALANCE,
                DATE_FORMAT,
                captured::add
        );

        converter.generate();

        assertEquals(1, captured.size());
        assertEquals("1", captured.getFirst().displayId());
        assertEquals("15.03", captured.getFirst().formattedDate());
        assertEquals("Test withdrawal", captured.getFirst().comment());
        assertEquals(MoneyAmount.ofCents(-500L), captured.getFirst().total());
    }

    @Test
    void generate_withMultipleBookings_producesOneRowPerBooking() {
        final Booking booking1 = DefaultBooking.builder()
                .id(1)
                .displayId(1)
                .date(LocalDate.of(2025, 3, 5))
                .type(BookingType.INTEREST)
                .amount(MoneyAmount.ofCents(100L))
                .comment("Interest")
                .build();

        final Booking booking2 = DefaultBooking.builder()
                .id(2)
                .displayId(2)
                .date(LocalDate.of(2025, 3, 20))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmount.ofCents(-200L))
                .comment("Withdrawal")
                .build();

        final FilteredBookings filteredBookings = new FilteredBookings(List.of(booking1, booking2));
        final List<BookingRow> captured = new ArrayList<>();

        final DefaultBookingsToBookingRowConverter converter = new DefaultBookingsToBookingRowConverter(
                filteredBookings,
                STARTING_BALANCE,
                DATE_FORMAT,
                captured::add
        );

        converter.generate();

        assertEquals(2, captured.size());
    }

    @Test
    void generate_bookingsOutOfDateOrder_areEmittedSortedByDate() {
        final Booking laterBooking = DefaultBooking.builder()
                .id(2)
                .displayId(2)
                .date(LocalDate.of(2025, 3, 20))
                .type(BookingType.INTEREST)
                .amount(MoneyAmount.ofCents(50L))
                .comment("Later")
                .build();

        final Booking earlierBooking = DefaultBooking.builder()
                .id(1)
                .displayId(1)
                .date(LocalDate.of(2025, 3, 5))
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmount.ofCents(-100L))
                .comment("Earlier")
                .build();

        // Supply them in reverse chronological order
        final FilteredBookings filteredBookings = new FilteredBookings(List.of(laterBooking, earlierBooking));
        final List<BookingRow> captured = new ArrayList<>();

        final DefaultBookingsToBookingRowConverter converter = new DefaultBookingsToBookingRowConverter(
                filteredBookings,
                STARTING_BALANCE,
                DATE_FORMAT,
                captured::add
        );

        converter.generate();

        // The first emitted row must be the earlier date
        assertEquals("05.03", captured.get(0).formattedDate());
        assertEquals("20.03", captured.get(1).formattedDate());
    }

    @Test
    void generate_withNoBookings_consumerIsNeverCalled() {
        final FilteredBookings filteredBookings = new FilteredBookings(List.of());
        final List<BookingRow> captured = new ArrayList<>();

        final DefaultBookingsToBookingRowConverter converter = new DefaultBookingsToBookingRowConverter(
                filteredBookings,
                STARTING_BALANCE,
                DATE_FORMAT,
                captured::add
        );

        converter.generate();

        assertEquals(0, captured.size());
    }

    @Test
    void generate_rowAmountsReflectBookingTypeAndAmount() {
        final MoneyAmount bookingAmount = MoneyAmount.ofCents(300L);
        final Booking booking = DefaultBooking.builder()
                .id(1)
                .displayId(1)
                .date(LocalDate.of(2025, 3, 10))
                .type(BookingType.COMPENSATION)
                .amount(bookingAmount)
                .comment("Compensation")
                .build();

        final FilteredBookings filteredBookings = new FilteredBookings(List.of(booking));
        final List<BookingRow> captured = new ArrayList<>();

        final DefaultBookingsToBookingRowConverter converter = new DefaultBookingsToBookingRowConverter(
                filteredBookings,
                STARTING_BALANCE,
                DATE_FORMAT,
                captured::add
        );

        converter.generate();

        // The amount for COMPENSATION should match; other types should be ZERO
        assertEquals(bookingAmount, captured.getFirst().amount(BookingType.COMPENSATION));
        assertEquals(MoneyAmount.ZERO, captured.getFirst().amount(BookingType.WITHDRAWAL));
        assertEquals(MoneyAmount.ZERO, captured.getFirst().amount(BookingType.INTEREST));
    }

    @Test
    void generate_displayIdMatchesBookingDisplayId() {
        final Booking booking = DefaultBooking.builder()
                .id(99)
                .displayId(42)
                .date(LocalDate.of(2025, 3, 1))
                .type(BookingType.TAX_CREDIT)
                .amount(MoneyAmount.ofCents(0L))
                .comment("")
                .build();

        final FilteredBookings filteredBookings = new FilteredBookings(List.of(booking));
        final List<BookingRow> captured = new ArrayList<>();

        final DefaultBookingsToBookingRowConverter converter = new DefaultBookingsToBookingRowConverter(
                filteredBookings,
                STARTING_BALANCE,
                DATE_FORMAT,
                captured::add
        );

        converter.generate();

        assertEquals("42", captured.getFirst().displayId());
    }

    @Test
    void generate_bookingReferenceIsPreserved() {
        final Booking booking = DefaultBooking.builder()
                .id(5)
                .displayId(5)
                .date(LocalDate.of(2025, 6, 15))
                .type(BookingType.INTEREST)
                .amount(MoneyAmount.ofCents(250L))
                .comment("June interest")
                .build();

        final FilteredBookings filteredBookings = new FilteredBookings(List.of(booking));
        final List<BookingRow> captured = new ArrayList<>();

        final DefaultBookingsToBookingRowConverter converter = new DefaultBookingsToBookingRowConverter(
                filteredBookings,
                STARTING_BALANCE,
                DATE_FORMAT,
                captured::add
        );

        converter.generate();

        assertEquals(List.of(booking), captured.getFirst().bookings());
    }

    @Test
    void generate_withZeroStartingBalance_totalReflectsBookingAmount() {
        final MoneyAmount bookingAmount = MoneyAmount.ofCents(-750L);
        final Booking booking = DefaultBooking.builder()
                .id(1)
                .displayId(1)
                .date(LocalDate.of(2025, 1, 1))
                .type(BookingType.WITHDRAWAL)
                .amount(bookingAmount)
                .comment("Withdrawal")
                .build();

        final FilteredBookings filteredBookings = new FilteredBookings(List.of(booking));
        final List<BookingRow> captured = new ArrayList<>();

        final DefaultBookingsToBookingRowConverter converter = new DefaultBookingsToBookingRowConverter(
                filteredBookings,
                MoneyAmount.ZERO,
                DATE_FORMAT,
                captured::add
        );

        converter.generate();

        // The row's total is the booking's amount
        assertEquals(bookingAmount, captured.getFirst().total());
    }
}