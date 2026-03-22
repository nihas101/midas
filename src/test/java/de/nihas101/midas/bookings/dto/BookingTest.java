package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.bookings.entity.BookingEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
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
        final Booking actual = Booking.fromEntity(entity);
        Assertions.assertEquals(expected, actual);
    }

    public static Stream<Arguments> fromEntityValues() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new BookingEntity(),
                        new Booking(
                                null,
                                null,
                                null,
                                null,
                                null,
                                MoneyAmount.ZERO,
                                null
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
                                "Test"
                        ),
                        new Booking(
                                1,
                                2,
                                3,
                                LocalDate.now(),
                                BookingType.COMPENSATION,
                                MoneyAmount.ofCents(100L),
                                "Test"
                        )
                )
        );
    }
}