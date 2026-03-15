package de.nihas101.midas.bookings.entity;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

class BookingEntityTest {

    @ParameterizedTest
    @MethodSource("fromEntityValues")
    void fromDto(Booking booking, ShareholderEntity shareholder, BookingEntity expected) {
        final BookingEntity actual = BookingEntity.fromDto(booking, shareholder);
        Assertions.assertEquals(expected, actual);
    }

    public static Stream<Arguments> fromEntityValues() {
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of(
                        new Booking(),
                        null,
                        new BookingEntity(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null
                        )

                ),
                Arguments.of(
                        new Booking(),
                        new ShareholderEntity(),
                        new BookingEntity(
                                null,
                                null,
                                new ShareholderEntity(),
                                null,
                                null,
                                null,
                                null
                        )

                ),
                Arguments.of(
                        new Booking(
                                1,
                                2,
                                3,
                                LocalDate.now(),
                                BookingType.COMPENSATION,
                                MoneyAmount.ofCents(100L),
                                "Test"
                        ),
                        new ShareholderEntity(3, null, null, null, null),
                        new BookingEntity(
                                1,
                                2,
                                new ShareholderEntity(3, null, null, null, null),
                                LocalDate.now(),
                                BookingType.COMPENSATION,
                                MoneyAmount.ofCents(100L),
                                "Test"
                        )
                )
        );
    }
}