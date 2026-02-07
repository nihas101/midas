package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.UI;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.userconfig.entity.UserConfig;
import de.nihas101.midas.userconfig.service.UserConfigReader;
import de.nihas101.midas.userconfig.service.UserConfigService;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MidasLocaleResolver {

    private final MidasConfig config;
    private final UserConfigReader userConfigReader;

    public MidasLocaleResolver(
            MidasConfig config,
            UserConfigReader userConfigReader
    ) {
        this.config = config;
        this.userConfigReader = userConfigReader;
    }

    public Locale resolve() {
        if (config.getI18n().isForceDefaultLanguage()) {
            return Locale.of(config.getI18n().getDefaultLocale());
        }

        return userConfigReader.findByUserIdentifier(UserConfigService.DEFAULT_USER)
                .map(UserConfig::getLocale)
                .map(Locale::forLanguageTag)
                .orElse(UI.getCurrent().getLocale());
    }
}
