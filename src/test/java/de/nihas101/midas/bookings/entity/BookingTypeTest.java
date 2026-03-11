package de.nihas101.midas.bookings.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingTypeTest {

    @ParameterizedTest
    @MethodSource("enumValues")
    void fromId(BookingType bookingType) {
        assertEquals(bookingType, BookingType.fromId(bookingType.getId()));
    }

    public static Stream<Arguments> enumValues() {
        return Arrays.stream(BookingType.values())
                .map(Arguments::of);
    }

    @Test
    void fromIdThrowsOnUnknown() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BookingType.fromId(999));
    }
}