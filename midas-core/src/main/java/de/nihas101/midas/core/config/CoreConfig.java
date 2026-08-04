package de.nihas101.midas.core.config;

import de.nihas101.midas.core.cleanup.CleanupConfig;
import de.nihas101.midas.core.sqlite.SqliteConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Data
@Component
@RequiredArgsConstructor
public class CoreConfig {
    private final TitleConfig title;
    private final ThemeConfig theme;
    private final UIConfig ui;
    private final I18nConfig i18n;
    private final CleanupConfig cleanup;
    private final SqliteConfig sqlite;

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
