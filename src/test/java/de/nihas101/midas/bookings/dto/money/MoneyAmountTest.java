package de.nihas101.midas.bookings.dto.money;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.stream.Stream;

class MoneyAmountTest {

    @ParameterizedTest
    @MethodSource("ofValues")
    void of(BigDecimal input, Long expected) {
        final MoneyAmount moneyAmount = MoneyAmount.of(input);
        Assertions.assertEquals(expected, moneyAmount.getCents());
    }

    public static Stream<Arguments> ofValues() {
        return Stream.of(
                Arguments.of(null, 0L),
                Arguments.of(new BigDecimal("1"), 100L),
                Arguments.of(new BigDecimal("0"), 0L),
                Arguments.of(new BigDecimal("1.00"), 100L),
                Arguments.of(new BigDecimal("1.01"), 101L),
                Arguments.of(new BigDecimal("0.01"), 1L),
                Arguments.of(new BigDecimal("0.00"), 0L),
                Arguments.of(new BigDecimal("0.001"), 0L),
                Arguments.of(new BigDecimal("100.37"), 10037L),
                Arguments.of(new BigDecimal("100.374"), 10037L),
                Arguments.of(new BigDecimal("100.375"), 10038L)
        );
    }

    @ParameterizedTest
    @MethodSource("getCentValues")
    void getCents(Long cents, Long expected) {
        final MoneyAmount moneyAmount = MoneyAmount.ofCents(cents);
        Assertions.assertEquals(expected, moneyAmount.getCents());
    }

    public static Stream<Arguments> getCentValues() {
        return Stream.of(
                Arguments.of(null, 0L),
                Arguments.of(cents("1"), 100L),
                Arguments.of(cents("0"), 0L),
                Arguments.of(cents("1.00"), 100L),
                Arguments.of(cents("1.01"), 101L),
                Arguments.of(cents("0.01"), 1L),
                Arguments.of(cents("0.00"), 0L),
                Arguments.of(cents("0.001"), 0L),
                Arguments.of(cents("100.37"), 10037L)
        );
    }

    private static long cents(final String amount) {
        return new BigDecimal(amount)
                .multiply(new BigDecimal("100"))
                .longValue();
    }

    @ParameterizedTest
    @MethodSource("plusValues")
    void plus(MoneyAmount a, MoneyAmount b, MoneyAmount expected) {
        Assertions.assertEquals(expected, a.plus(b));
    }

    public static Stream<Arguments> plusValues() {
        return Stream.of(
                Arguments.of(MoneyAmount.ofCents(0L), MoneyAmount.ofCents(0L), MoneyAmount.ofCents(0L)),
                Arguments.of(MoneyAmount.ofCents(1L), MoneyAmount.ofCents(-1L), MoneyAmount.ofCents(0L)),
                Arguments.of(MoneyAmount.ofCents(1L), MoneyAmount.ofCents(-1L), MoneyAmount.ofCents(0L)),
                Arguments.of(MoneyAmount.ofCents(-1L), MoneyAmount.ofCents(-1L), MoneyAmount.ofCents(-2L)),
                Arguments.of(MoneyAmount.ofCents(1L), null, MoneyAmount.ofCents(1L)),
                Arguments.of(MoneyAmount.ofCents(100L), MoneyAmount.ofCents(50L), MoneyAmount.ofCents(150L))
        );
    }

    @ParameterizedTest
    @MethodSource("minusValues")
    void minus(MoneyAmount a, MoneyAmount b, MoneyAmount expected) {
        Assertions.assertEquals(expected, a.minus(b));
    }

    public static Stream<Arguments> minusValues() {
        return Stream.of(
                Arguments.of(MoneyAmount.ofCents(0L), MoneyAmount.ofCents(0L), MoneyAmount.ofCents(0L)),
                Arguments.of(MoneyAmount.ofCents(1L), MoneyAmount.ofCents(1L), MoneyAmount.ofCents(0L)),
                Arguments.of(MoneyAmount.ofCents(-1L), MoneyAmount.ofCents(-1L), MoneyAmount.ofCents(0L)),
                Arguments.of(MoneyAmount.ofCents(1L), MoneyAmount.ofCents(-1L), MoneyAmount.ofCents(2L)),
                Arguments.of(MoneyAmount.ofCents(1L), null, MoneyAmount.ofCents(1L)),
                Arguments.of(MoneyAmount.ofCents(100L), MoneyAmount.ofCents(50L), MoneyAmount.ofCents(50L))
        );
    }

    @ParameterizedTest
    @MethodSource("toBigDecimalValues")
    void toBigDecimal(MoneyAmount moneyAmount, BigDecimal expected) {
        Assertions.assertEquals(expected, moneyAmount.toBigDecimal());
    }

    public static Stream<Arguments> toBigDecimalValues() {
        return Stream.of(
                Arguments.of(MoneyAmount.ofCents(null), new BigDecimal("0.0000")),
                Arguments.of(MoneyAmount.ofCents(100L), new BigDecimal("1.0000")),
                Arguments.of(MoneyAmount.ofCents(0L), new BigDecimal("0.0000")),
                Arguments.of(MoneyAmount.ofCents(-100L), new BigDecimal("-1.0000"))
        );
    }

    @ParameterizedTest
    @MethodSource("formatValues")
    void format(MoneyAmount moneyAmount, Locale locale, String expected) {
        Assertions.assertEquals(expected, moneyAmount.format(locale));
    }

    public static Stream<Arguments> formatValues() {
        return Stream.of(
                Arguments.of(MoneyAmount.ofCents(null), Locale.of("en-EN"), "0.00"),
                Arguments.of(MoneyAmount.ofCents(100L), Locale.of("en-EN"), "1.00"),
                Arguments.of(MoneyAmount.ofCents(0L), Locale.of("en-EN"), "0.00"),
                Arguments.of(MoneyAmount.ofCents(-100L), Locale.of("en-EN"), "-1.00"),

                Arguments.of(MoneyAmount.ofCents(null), Locale.of("de-DE"), "0.00"),
                Arguments.of(MoneyAmount.ofCents(100L), Locale.of("de-DE"), "1.00"),
                Arguments.of(MoneyAmount.ofCents(0L), Locale.of("de-DE"), "0.00"),
                Arguments.of(MoneyAmount.ofCents(-100L), Locale.of("de-DE"), "-1.00")
        );
    }

    @ParameterizedTest
    @MethodSource("toStringValues")
    void testToString(MoneyAmount moneyAmount, String expected) {
        Assertions.assertEquals(expected, moneyAmount.toString());
    }

    public static Stream<Arguments> toStringValues() {
        return Stream.of(
                Arguments.of(MoneyAmount.ofCents(null), "0.00"),
                Arguments.of(MoneyAmount.ofCents(100L), "1.00"),
                Arguments.of(MoneyAmount.ofCents(0L), "0.00"),
                Arguments.of(MoneyAmount.ofCents(-100L), "-1.00")
        );
    }
}