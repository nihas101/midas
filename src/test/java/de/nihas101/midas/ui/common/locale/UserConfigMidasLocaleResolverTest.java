package de.nihas101.midas.ui.common.locale;

import de.nihas101.midas.userconfig.entity.UserConfig;
import de.nihas101.midas.userconfig.service.UserConfigReader;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserConfigMidasLocaleResolverTest {

    @ParameterizedTest
    @MethodSource("resolveValues")
    void resolve(final UserConfigReader userConfigReader, final Locale expected) {
        final UserConfigMidasLocaleResolver localeResolver = new UserConfigMidasLocaleResolver(userConfigReader);
        final Locale resolve = localeResolver.resolve();
        assertEquals(expected, resolve);
    }

    public static Stream<Arguments> resolveValues() {
        return Stream.of(
                Arguments.of((UserConfigReader) any -> Optional.empty(), null),
                Arguments.of((UserConfigReader) any -> Optional.of(new UserConfig()), null),
                Arguments.of((UserConfigReader) any -> {
                    final UserConfig userConfig = UserConfig.builder().locale("").build();
                    return Optional.of(userConfig);
                }, null),
                Arguments.of((UserConfigReader) any -> {
                    final UserConfig userConfig = UserConfig.builder().locale("         ").build();
                    return Optional.of(userConfig);
                }, null),
                Arguments.of((UserConfigReader) any -> {
                    final UserConfig userConfig = UserConfig.builder().locale("notAValidLocale").build();
                    return Optional.of(userConfig);
                }, null),
                Arguments.of((UserConfigReader) any -> {
                    final UserConfig userConfig = UserConfig.builder().locale("de").build();
                    return Optional.of(userConfig);
                }, Locale.GERMAN)
        );
    }
}