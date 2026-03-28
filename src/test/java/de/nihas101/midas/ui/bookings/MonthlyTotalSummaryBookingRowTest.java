package de.nihas101.midas.ui.bookings;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.MonthlyBookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.entity.Source;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotalSum;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static de.nihas101.midas.bookings.entity.BookingType.COMPENSATION;
import static de.nihas101.midas.bookings.entity.BookingType.INTEREST;
import static de.nihas101.midas.bookings.entity.BookingType.TAX_CREDIT;
import static de.nihas101.midas.bookings.entity.BookingType.TAX_PREVIOUS_YEAR;
import static de.nihas101.midas.bookings.entity.BookingType.WITHDRAWAL;

class MonthlyTotalSummaryBookingRowTest {

    @Test
    public void comment_null() {
        final Bookings bookings = createBookings(null, null);
        final MonthlySummaryBookingRow bookingRow = new MonthlySummaryBookingRow(
                null,
                bookings,
                Month.JANUARY
        );

        Assertions.assertNull(bookingRow.comment());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "comment"})
    public void comment(final String expectedComment) {
        final Bookings bookings = createBookings(null, null);
        final MonthlySummaryBookingRow bookingRow = new MonthlySummaryBookingRow(
                expectedComment,
                bookings,
                Month.JANUARY
        );

        Assertions.assertEquals(expectedComment, bookingRow.comment());
    }

    @ParameterizedTest
    @MethodSource("totalValues")
    public void total(final Bookings bookings, MoneyAmount expected) {
        final MonthlySummaryBookingRow bookingRow = new MonthlySummaryBookingRow(
                "",
                bookings,
                Month.MARCH
        );

        Assertions.assertEquals(expected, bookingRow.total());
    }

    public static Stream<Arguments> totalValues() {
        return Stream.of(
                Arguments.of(createBookings(null, null), MoneyAmount.ZERO),
                Arguments.of(createBookings(null, Collections.emptyList()), MoneyAmount.ZERO),
                Arguments.of(createBookings(null,
                        List.of(
                                new Booking(1,
                                        1,
                                        1,
                                        LocalDate.of(2026, 3, 14),
                                        COMPENSATION,
                                        MoneyAmount.ofCents(100L),
                                        "comment",
                                        Source.USER
                                )
                        )), MoneyAmount.ofCents(100L)),
                Arguments.of(createBookings(null,
                        List.of(
                                new Booking(1,
                                        1,
                                        1,
                                        LocalDate.of(2026, 3, 14),
                                        COMPENSATION,
                                        MoneyAmount.ofCents(100L),
                                        "comment",
                                        Source.USER
                                ),
                                new Booking(2,
                                        2,
                                        1,
                                        LocalDate.of(2026, 3, 14),
                                        COMPENSATION,
                                        MoneyAmount.ofCents(10L),
                                        "comment",
                                        Source.USER
                                )
                        )), MoneyAmount.ofCents(110L)),
                Arguments.of(createBookings(null,
                        List.of(
                                new Booking(1,
                                        1,
                                        1,
                                        LocalDate.of(2026, 3, 14),
                                        COMPENSATION,
                                        MoneyAmount.ofCents(100L),
                                        "comment",
                                        Source.USER
                                ),
                                new Booking(2,
                                        2,
                                        1,
                                        LocalDate.of(2026, 3, 15),
                                        WITHDRAWAL,
                                        MoneyAmount.ofCents(-10L),
                                        "comment",
                                        Source.USER
                                )
                        )), MoneyAmount.ofCents(90L)),
                Arguments.of(createBookings(MoneyAmount.ofCents(1000L),
                        List.of(
                                new Booking(1,
                                        1,
                                        1,
                                        LocalDate.of(2026, 3, 14),
                                        COMPENSATION,
                                        MoneyAmount.ofCents(100L),
                                        "comment",
                                        Source.USER
                                ),
                                new Booking(2,
                                        2,
                                        1,
                                        LocalDate.of(2026, 3, 15),
                                        WITHDRAWAL,
                                        MoneyAmount.ofCents(-10L),
                                        "comment",
                                        Source.USER
                                )
                        )), MoneyAmount.ofCents(90L))
        );
    }

    @ParameterizedTest
    @MethodSource("amountsValues")
    public void amounts(final Bookings bookings, Map<BookingType, MoneyAmount> expected) {
        final MonthlySummaryBookingRow bookingRow = new MonthlySummaryBookingRow(
                "",
                bookings,
                Month.MARCH
        );

        Assertions.assertEquals(new MonthlyTotalSum(expected), bookingRow.amounts());
    }

    public static Stream<Arguments> amountsValues() {
        return Stream.of(
                Arguments.of(
                        createBookings(null, null),
                        Map.of(
                                WITHDRAWAL, MoneyAmount.ofCents(0L),
                                TAX_PREVIOUS_YEAR, MoneyAmount.ofCents(0L),
                                TAX_CREDIT, MoneyAmount.ofCents(0L),
                                INTEREST, MoneyAmount.ofCents(0L),
                                COMPENSATION, MoneyAmount.ofCents(0L)
                        )
                ),
                Arguments.of(
                        createBookings(null, Collections.emptyList()),
                        Map.of(
                                WITHDRAWAL, MoneyAmount.ofCents(0L),
                                TAX_PREVIOUS_YEAR, MoneyAmount.ofCents(0L),
                                TAX_CREDIT, MoneyAmount.ofCents(0L),
                                INTEREST, MoneyAmount.ofCents(0L),
                                COMPENSATION, MoneyAmount.ofCents(0L)
                        )
                ),
                Arguments.of(
                        createBookings(null,
                                List.of(
                                        new Booking(1,
                                                1,
                                                1,
                                                LocalDate.of(2026, 3, 14),
                                                COMPENSATION,
                                                MoneyAmount.ofCents(100L),
                                                "comment",
                                                Source.USER
                                        )
                                )),
                        Map.of(
                                WITHDRAWAL, MoneyAmount.ofCents(0L),
                                TAX_PREVIOUS_YEAR, MoneyAmount.ofCents(0L),
                                TAX_CREDIT, MoneyAmount.ofCents(0L),
                                INTEREST, MoneyAmount.ofCents(0L),
                                COMPENSATION, MoneyAmount.ofCents(100L)
                        )),
                Arguments.of(
                        createBookings(null,
                                List.of(
                                        new Booking(1,
                                                1,
                                                1,
                                                LocalDate.of(2026, 3, 14),
                                                COMPENSATION,
                                                MoneyAmount.ofCents(100L),
                                                "comment",
                                                Source.USER
                                        ),
                                        new Booking(2,
                                                2,
                                                1,
                                                LocalDate.of(2026, 3, 14),
                                                COMPENSATION,
                                                MoneyAmount.ofCents(10L),
                                                "comment",
                                                Source.USER
                                        )
                                )
                        ),
                        Map.of(
                                WITHDRAWAL, MoneyAmount.ofCents(0L),
                                TAX_PREVIOUS_YEAR, MoneyAmount.ofCents(0L),
                                TAX_CREDIT, MoneyAmount.ofCents(0L),
                                INTEREST, MoneyAmount.ofCents(0L),
                                COMPENSATION, MoneyAmount.ofCents(110L)
                        )),
                Arguments.of(
                        createBookings(null,
                                List.of(
                                        new Booking(1,
                                                1,
                                                1,
                                                LocalDate.of(2026, 3, 14),
                                                COMPENSATION,
                                                MoneyAmount.ofCents(100L),
                                                "comment",
                                                Source.USER
                                        ),
                                        new Booking(2,
                                                2,
                                                1,
                                                LocalDate.of(2026, 3, 15),
                                                WITHDRAWAL,
                                                MoneyAmount.ofCents(-10L),
                                                "comment",
                                                Source.USER
                                        )
                                )
                        ),
                        Map.of(
                                WITHDRAWAL, MoneyAmount.ofCents(-10L),
                                TAX_PREVIOUS_YEAR, MoneyAmount.ofCents(0L),
                                TAX_CREDIT, MoneyAmount.ofCents(0L),
                                INTEREST, MoneyAmount.ofCents(0L),
                                COMPENSATION, MoneyAmount.ofCents(100L)
                        )),
                Arguments.of(
                        createBookings(MoneyAmount.ofCents(1000L),
                                List.of(
                                        new Booking(1,
                                                1,
                                                1,
                                                LocalDate.of(2026, 3, 14),
                                                COMPENSATION,
                                                MoneyAmount.ofCents(100L),
                                                "comment",
                                                Source.USER
                                        ),
                                        new Booking(2,
                                                2,
                                                1,
                                                LocalDate.of(2026, 3, 15),
                                                WITHDRAWAL,
                                                MoneyAmount.ofCents(-10L),
                                                "comment",
                                                Source.USER
                                        )
                                )
                        ),
                        Map.of(
                                WITHDRAWAL, MoneyAmount.ofCents(-10L),
                                TAX_PREVIOUS_YEAR, MoneyAmount.ofCents(0L),
                                TAX_CREDIT, MoneyAmount.ofCents(0L),
                                INTEREST, MoneyAmount.ofCents(0L),
                                COMPENSATION, MoneyAmount.ofCents(100L)
                        ))
        );
    }

    private static Bookings createBookings(final MoneyAmount initialBalance, final List<Booking> bookingsInMonth) {
        return new Bookings() {
            @Override
            public OpeningBalance openingBalance() {
                return new OpeningBalance(initialBalance);
            }

            @Override
            public MonthlyBookings bookingsInMonth(final Month month) {
                return new MonthlyBookings(bookingsInMonth);
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };
    }

}