package de.nihas101.midas.ui.common.locale;

import de.nihas101.midas.userconfig.entity.UserConfig;
import de.nihas101.midas.userconfig.service.UserConfigReader;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@RequiredArgsConstructor
public class UserConfigMidasLocaleResolver implements MidasLocaleResolver {

    private final InvalidLocale invalidLocale;
    private final UserConfigReader userConfigReader;

    public UserConfigMidasLocaleResolver(UserConfigReader userConfigReader) {
        this(
                new InvalidLocale(),
                userConfigReader
        );
    }

    @Override
    public Locale resolve() {
        final Locale locale = userConfigReader.findByUserIdentifier(UserConfigService.DEFAULT_USER)
                .map(UserConfig::getLocale)
                .map(Locale::forLanguageTag)
                .orElse(null);
        if (invalidLocale.corresponds(locale)) {
            return null;
        }

        return locale;
    }
}
