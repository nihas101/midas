package de.nihas101.midas.bookings.dto.money;

import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.money.MoneyAmountConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class MoneyAmountConverterTest {

    private final MoneyAmountConverter moneyAmountConverter = new MoneyAmountConverter();

    @ParameterizedTest
    @MethodSource("convertToDatabaseColumnValues")
    void convertToDatabaseColumn(
            final MoneyAmount amount,
            final Long expected
    ) {
        final Long databaseAmount = moneyAmountConverter.convertToDatabaseColumn(amount);
        Assertions.assertEquals(expected, databaseAmount);
    }

    public static Stream<Arguments> convertToDatabaseColumnValues() {
        return Stream.of(
                Arguments.of(MoneyAmount.ofCents(0L), 0L),
                Arguments.of(MoneyAmount.ofCents(100L), 100L),
                Arguments.of(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("convertToEntityAttributeValues")
    void convertToEntityAttribute(
            final Long cents,
            final MoneyAmount expected
    ) {
        final MoneyAmount moneyAmount = moneyAmountConverter.convertToEntityAttribute(cents);
        Assertions.assertEquals(expected, moneyAmount);
    }

    public static Stream<Arguments> convertToEntityAttributeValues() {
        return Stream.of(
                Arguments.of(0L, MoneyAmount.ofCents(0L)),
                Arguments.of(100L, MoneyAmount.ofCents(100L)),
                Arguments.of(null, MoneyAmount.ZERO)
        );
    }
}