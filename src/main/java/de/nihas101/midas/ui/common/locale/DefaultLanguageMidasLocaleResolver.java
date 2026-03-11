package de.nihas101.midas.ui.common.locale;

import de.nihas101.midas.config.I18nConfig;
import de.nihas101.midas.config.MidasConfig;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
class DefaultLanguageMidasLocaleResolver implements MidasLocaleResolver {

    private final Locale locale;

    public DefaultLanguageMidasLocaleResolver(final MidasConfig config) {
        this(config, new InvalidLocale());
    }

    public DefaultLanguageMidasLocaleResolver(final MidasConfig config, InvalidLocale invalidLocale) {
        this(
                Optional.ofNullable(config)
                        .map(MidasConfig::getI18n)
                        .map(I18nConfig::getDefaultLocale)
                        .map(Locale::forLanguageTag)
                        .filter(l -> !invalidLocale.corresponds(l))
                        .orElse(null)
        );
    }

    @Override
    public Locale resolve() {
        return locale;
    }
}
