package de.nihas101.midas.ui.common.locale;

import com.vaadin.flow.component.UI;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.userconfig.service.UserConfigReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MidasLocaleResolverConfig {

    @Bean
    @ConditionalOnProperty(value = "midas.i18n.force-default-language", havingValue = "true")
    public MidasLocaleResolver constantLanguageMidasLocaleResolver(final MidasConfig config) {
        return new DefaultLanguageMidasLocaleResolver(config);
    }

    @Bean
    @org.springframework.context.annotation.Fallback
    public MidasLocaleResolver userConfigMidasLocaleResolver(final UserConfigReader userConfigReader) {
        return new Fallback(
                new UserConfigMidasLocaleResolver(userConfigReader),
                () -> UI.getCurrent().getLocale()
        );
    }

}
