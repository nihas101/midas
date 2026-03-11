package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

class CachingBookingsTest {

    @Test
    void initialBalanceIsCached() {
        List<Booking> bookings = List.of(
                new Booking(
                        1,
                        2,
                        3,
                        LocalDate.now(),
                        BookingType.WITHDRAWAL,
                        MoneyAmount.ofCents(100L),
                        "Test"),
                new Booking(
                        2,
                        3,
                        3,
                        LocalDate.now(),
                        BookingType.WITHDRAWAL,
                        MoneyAmount.ofCents(150L),
                        "Test"
                )
        );
        final MoneyAmount expectedInitialBalance = MoneyAmount.ofCents(100L);
        final CachingBookings cachingBookings = new CachingBookings(getBookings(expectedInitialBalance, bookings));

        Assertions.assertEquals(expectedInitialBalance, cachingBookings.initialBalance());
        Assertions.assertEquals(expectedInitialBalance, cachingBookings.initialBalance());
    }

    @Test
    void bookingsInMonthIsCached() {
        List<Booking> bookings = List.of(
                new Booking(
                        1,
                        2,
                        3,
                        LocalDate.now(),
                        BookingType.WITHDRAWAL,
                        MoneyAmount.ofCents(100L),
                        "Test"),
                new Booking(
                        2,
                        3,
                        3,
                        LocalDate.now(),
                        BookingType.WITHDRAWAL,
                        MoneyAmount.ofCents(150L),
                        "Test"
                )
        );
        final CachingBookings cachingBookings = new CachingBookings(getBookings(MoneyAmount.ofCents(100L), bookings));

        Assertions.assertEquals(new MonthlyBookings(bookings), cachingBookings.bookingsInMonth(Month.JANUARY));
        Assertions.assertEquals(new MonthlyBookings(bookings), cachingBookings.bookingsInMonth(Month.JANUARY));

        final MonthlyBookings februaryBookings = cachingBookings.bookingsInMonth(Month.FEBRUARY);
        Assertions.assertNotEquals(new MonthlyBookings(bookings), februaryBookings);
        Assertions.assertEquals(februaryBookings, cachingBookings.bookingsInMonth(Month.FEBRUARY));
    }

    private static Bookings getBookings(final MoneyAmount initialBalance, final List<Booking> bookings) {
        return new Bookings() {

            private MoneyAmount init = initialBalance;
            private List<Booking> book = bookings;

            @Override
            public MoneyAmount initialBalance() {
                final MoneyAmount ret = init;
                // Every call will return another value
                init = init.plus(MoneyAmount.ofCents(100L));
                return ret;
            }

            @Override
            public MonthlyBookings bookingsInMonth(final Month month) {
                final List<Booking> ret = book;
                book = new ArrayList<>(book);
                final Booking last = book.getLast();
                book.add(
                        Booking.builder()
                                .id(last.getId() + 1)
                                .displayId(last.getDisplayId())
                                .shareholderId(last.getShareholderId())
                                .amount(last.getAmount())
                                .type(BookingType.TAX_CREDIT)
                                .date(last.getDate().plusDays(1))
                                .comment(last.getComment())
                                .build()
                );
                return new MonthlyBookings(ret);
            }
        };
    }
}