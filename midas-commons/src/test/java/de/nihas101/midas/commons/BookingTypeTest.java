package de.nihas101.midas.commons;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingTypeTest {

    // Ensure every type is covered
    @ParameterizedTest
    @EnumSource(BookingType.class)
    void fromId(BookingType input) {
        final BookingType output = BookingType.fromId(input.getId());
        assertEquals(output, input);
    }

    // Ensure no accidental change happens for the existing types
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void fromId(int input) {
        final BookingType output = BookingType.fromId(input);
        assertNotNull(output);
    }

    @Test
    void fromInvalidId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> BookingType.fromId(-1));
    }

    @Test
    void creatableByUser() {
        Assertions.assertEquals(
                List.of(
                        BookingType.WITHDRAWAL,
                        BookingType.TAX_PREVIOUS_YEAR,
                        BookingType.TAX_CREDIT,
                        BookingType.INTEREST,
                        BookingType.COMPENSATION
                ),
                BookingType.creatableByUser()
        );
    }
}