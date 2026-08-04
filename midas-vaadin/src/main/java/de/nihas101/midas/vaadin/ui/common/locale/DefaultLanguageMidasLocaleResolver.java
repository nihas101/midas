package de.nihas101.midas.vaadin.ui.common.locale;

import de.nihas101.midas.core.config.CoreConfig;
import de.nihas101.midas.core.config.I18nConfig;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
public class DefaultLanguageMidasLocaleResolver implements MidasLocaleResolver {

    private final Locale locale;

    public DefaultLanguageMidasLocaleResolver(final CoreConfig config) {
        this(config, new InvalidLocale());
    }

    public DefaultLanguageMidasLocaleResolver(final CoreConfig config, InvalidLocale invalidLocale) {
        this(
                Optional.ofNullable(config)
                        .map(CoreConfig::getI18n)
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
