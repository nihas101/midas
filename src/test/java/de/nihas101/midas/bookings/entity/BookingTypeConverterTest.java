package de.nihas101.midas.bookings.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingTypeConverterTest {

    private final BookingTypeConverter converter = new BookingTypeConverter();

    @ParameterizedTest
    @EnumSource(BookingType.class)
    void convertToDatabaseColumn(BookingType type) {
        assertEquals(type.getId(), converter.convertToDatabaseColumn(type));
    }

    @Test
    void convertToDatabaseColumn_null() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @ParameterizedTest
    @EnumSource(BookingType.class)
    void convertToEntityAttribute(BookingType type) {
        assertEquals(type, converter.convertToEntityAttribute(type.getId()));
    }

    @Test
    void convertToEntityAttribute_null() {
        assertNull(converter.convertToEntityAttribute(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 6, 32, 99})
    void convertToEntityAttribute_invalidId(int invalidId) {
        assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute(invalidId));
    }
}
