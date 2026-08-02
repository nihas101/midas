package de.nihas101.midas.ui.common.locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.stream.Stream;

class FallbackTest {

    @ParameterizedTest
    @MethodSource("resolveValues")
    void resolve(final MidasLocaleResolver primaryResolver, final MidasLocaleResolver alternative, final Locale expectedLocale) {
        final Fallback fallBack = new Fallback(primaryResolver, alternative);
        Assertions.assertEquals(expectedLocale, fallBack.resolve());
    }

    public static Stream<Arguments> resolveValues() {
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of((MidasLocaleResolver) () -> null, null, null),
                Arguments.of((MidasLocaleResolver) () -> null, (MidasLocaleResolver) () -> null, null),
                Arguments.of((MidasLocaleResolver) () -> null, (MidasLocaleResolver) () -> Locale.ENGLISH, Locale.ENGLISH),
                Arguments.of((MidasLocaleResolver) () -> Locale.ENGLISH, (MidasLocaleResolver) () -> Locale.GERMAN, Locale.ENGLISH)
        );
    }
}