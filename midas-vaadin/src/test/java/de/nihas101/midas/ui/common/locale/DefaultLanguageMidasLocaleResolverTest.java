package de.nihas101.midas.ui.common.locale;

import de.nihas101.midas.core.cleanup.CleanupConfig;
import de.nihas101.midas.core.config.AccountStatementConfig;
import de.nihas101.midas.core.config.CoreConfig;
import de.nihas101.midas.core.config.I18nConfig;
import de.nihas101.midas.core.config.ThemeConfig;
import de.nihas101.midas.core.config.TitleConfig;
import de.nihas101.midas.core.config.UIConfig;
import de.nihas101.midas.persistance.DbConfig;
import de.nihas101.midas.vaadin.ui.common.locale.DefaultLanguageMidasLocaleResolver;
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
    void resolveFromSettings(final CoreConfig config, Locale expectedLocale) {
        final DefaultLanguageMidasLocaleResolver localeResolver = new DefaultLanguageMidasLocaleResolver(config);
        Assertions.assertEquals(expectedLocale, localeResolver.resolve());
    }

    public static Stream<Arguments> resolveFromSettingsValues() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(
                        new CoreConfig(
                                new TitleConfig(),
                                new ThemeConfig(),
                                new UIConfig(),
                                new I18nConfig(),
                                new CleanupConfig(),
                                new DbConfig() {
                                },
                                new AccountStatementConfig()
                        ), Locale.ENGLISH
                ),
                Arguments.of(
                        new CoreConfig(
                                new TitleConfig(),
                                new ThemeConfig(),
                                new UIConfig(),
                                new I18nConfig("", false),
                                new CleanupConfig(),
                                new DbConfig() {
                                },
                                new AccountStatementConfig()
                        ), null
                ),
                Arguments.of(
                        new CoreConfig(
                                new TitleConfig(),
                                new ThemeConfig(),
                                new UIConfig(),
                                new I18nConfig("        ", false),
                                new CleanupConfig(),
                                new DbConfig() {
                                },
                                new AccountStatementConfig()
                        ), null
                ),
                Arguments.of(
                        new CoreConfig(
                                new TitleConfig(),
                                new ThemeConfig(),
                                new UIConfig(),
                                new I18nConfig("notAValidLocale", false),
                                new CleanupConfig(),
                                new DbConfig() {
                                },
                                new AccountStatementConfig()
                        ), null
                ),
                Arguments.of(
                        new CoreConfig(
                                new TitleConfig(),
                                new ThemeConfig(),
                                new UIConfig(),
                                new I18nConfig("en", false),
                                new CleanupConfig(),
                                new DbConfig() {
                                },
                                new AccountStatementConfig()
                        ), Locale.ENGLISH
                ),
                Arguments.of(
                        new CoreConfig(
                                new TitleConfig(),
                                new ThemeConfig(),
                                new UIConfig(),
                                new I18nConfig("de", false),
                                new CleanupConfig(),
                                new DbConfig() {
                                },
                                new AccountStatementConfig()
                        ), Locale.GERMAN
                )
        );
    }

}