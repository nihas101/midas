package de.nihas101.midas.core.bookings.entity;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingType;
import de.nihas101.midas.api.money.MoneyAmount;
import de.nihas101.midas.core.bookings.dto.DefaultBooking;
import de.nihas101.midas.core.shareholders.entity.ShareholderEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static de.nihas101.midas.api.bookings.Source.USER;

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
                        new DefaultBooking(),
                        null,
                        new BookingEntity(
                                null,
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
                        new DefaultBooking(),
                        new ShareholderEntity(),
                        new BookingEntity(
                                null,
                                null,
                                new ShareholderEntity(),
                                null,
                                null,
                                null,
                                null,
                                null
                        )

                ),
                Arguments.of(
                        new DefaultBooking(
                                1,
                                2,
                                3,
                                LocalDate.now(),
                                BookingType.COMPENSATION,
                                MoneyAmount.ofCents(100L),
                                "Test",
                                USER
                        ),
                        new ShareholderEntity(3, null, null, null),
                        new BookingEntity(
                                1,
                                2,
                                new ShareholderEntity(3, null, null, null),
                                LocalDate.now(),
                                BookingType.COMPENSATION,
                                MoneyAmount.ofCents(100L),
                                "Test",
                                USER
                        )
                )
        );
    }
}