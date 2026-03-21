package de.nihas101.midas.ui.common.locale;

import de.nihas101.midas.config.DesktopConfig;
import de.nihas101.midas.config.I18nConfig;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.config.ThemeConfig;
import de.nihas101.midas.config.UIConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.stream.Stream;

class DefaultLanguageMidasLocaleResolverTest {

    @ParameterizedTest
    @MethodSource("resolveFromLocaleValues")
    void resolveFromLocale(Locale locale, Locale expectedLocale) {
        final DefaultLanguageMidasLocaleResolver localeResolver = new DefaultLanguageMidasLocaleResolver(locale);
        Assertions.assertEquals(expectedLocale, localeResolver.resolve());
    }

    public static Stream<Arguments> resolveFromLocaleValues() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(Locale.ENGLISH, Locale.ENGLISH)
        );
    }

    @ParameterizedTest
    @MethodSource("resolveFromSettingsValues")
    void resolveFromSettings(final MidasConfig config, Locale expectedLocale) {
        final DefaultLanguageMidasLocaleResolver localeResolver = new DefaultLanguageMidasLocaleResolver(config);
        Assertions.assertEquals(expectedLocale, localeResolver.resolve());
    }

    public static Stream<Arguments> resolveFromSettingsValues() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new MidasConfig(
                                new ThemeConfig(),
                                new UIConfig(),
                                new I18nConfig(),
                                new DesktopConfig()
                        ), Locale.ENGLISH
                ),
                Arguments.of(
                        new MidasConfig(
                                new ThemeConfig(),
                                new UIConfig(),
                                new I18nConfig("", false),
                                new DesktopConfig()
                        ), null
                ),
                Arguments.of(
                        new MidasConfig(
                                new ThemeConfig(),
                                new UIConfig(),
                                new I18nConfig("        ", false),
                                new DesktopConfig()
                        ), null
                ),
                Arguments.of(
                        new MidasConfig(
                                new ThemeConfig(),
                                new UIConfig(),
                                new I18nConfig("notAValidLocale", false),
                                new DesktopConfig()
                        ), null
                ),
                Arguments.of(
                        new MidasConfig(
                                new ThemeConfig(),
                                new UIConfig(),
                                new I18nConfig("en", false),
                                new DesktopConfig()
                        ), Locale.ENGLISH
                ),
                Arguments.of(
                        new MidasConfig(
                                new ThemeConfig(),
                                new UIConfig(),
                                new I18nConfig("de", false),
                                new DesktopConfig()
                        ), Locale.GERMAN
                )
        );
    }

}