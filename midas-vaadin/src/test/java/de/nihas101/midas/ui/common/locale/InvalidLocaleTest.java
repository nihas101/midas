package de.nihas101.midas.ui.common.locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.stream.Stream;

class InvalidLocaleTest {

    @ParameterizedTest
    @MethodSource("testCorrespondsValues")
    void testAnalogous_To_compareToEnglish(final Locale locale, boolean expected) {
        Assertions.assertEquals(expected, new InvalidLocale().corresponds(locale));
    }

    public static Stream<Arguments> testCorrespondsValues() {
        return Stream.of(
                Arguments.of(Locale.ENGLISH, false),
                Arguments.of(Locale.of(""), true),
                Arguments.of(null, true)
        );
    }

    @Test
    void testAnalogous_To_compareToInvalid() {
        Assertions.assertTrue(new InvalidLocale().corresponds(new InvalidLocale()));
    }
}