package de.nihas101.midas.backup.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Stream;

class LocalDateTimeConverterTest {

    private final LocalDateTimeConverter converter = new LocalDateTimeConverter();

    @ParameterizedTest
    @MethodSource("convertToDatabaseColumnArguments")
    void convertToDatabaseColumn(
            final LocalDateTime localDateTime,
            final Long expected
    ) {
        Assertions.assertEquals(expected, converter.convertToDatabaseColumn(localDateTime));
    }

    public static Stream<Arguments> convertToDatabaseColumnArguments() {
        final LocalDateTime dateTime = LocalDateTime.of(2026, 4, 4, 12, 0, 0);
        final Long expectedMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(dateTime, expectedMillis)
        );
    }

    @ParameterizedTest
    @MethodSource("convertToEntityAttributeArguments")
    void convertToEntityAttribute(
            final Long millis,
            final LocalDateTime expected
    ) {
        Assertions.assertEquals(expected, converter.convertToEntityAttribute(millis));
    }

    public static Stream<Arguments> convertToEntityAttributeArguments() {
        final long millis = 1712232000000L;
        final LocalDateTime expectedDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());

        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(millis, expectedDateTime)
        );
    }
}
