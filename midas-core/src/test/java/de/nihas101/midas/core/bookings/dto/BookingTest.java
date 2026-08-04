package de.nihas101.midas.core.bookings.dto;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.persistance.bookings.BookingEntity;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

class BookingTest {

    @ParameterizedTest
    @MethodSource("fromEntityValues")
    void fromEntity(BookingEntity entity, Booking expected) {
        final Booking actual = DefaultBooking.fromEntity(entity);
        Assertions.assertEquals(expected, actual);
    }

    public static Stream<Arguments> fromEntityValues() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new BookingEntity(),
                        new DefaultBooking(
                                null,
                                null,
                                null,
                                null,
                                null,
                                MoneyAmount.ZERO,
                                null,
                                Source.USER
                        )
                ),
                Arguments.of(
                        new BookingEntity(
                                1,
                                2,
                                new ShareholderEntity(3, null, null, null),
                                LocalDate.now(),
                                BookingType.COMPENSATION,
                                MoneyAmount.ofCents(100L),
                                "Test",
                                Source.USER
                        ),
                        new DefaultBooking(
                                1,
                                2,
                                3,
                                LocalDate.now(),
                                BookingType.COMPENSATION,
                                MoneyAmount.ofCents(100L),
                                "Test",
                                Source.USER
                        )
                )
        );
    }
}