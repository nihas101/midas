package de.nihas101.midas.core.config;

import de.nihas101.midas.core.cleanup.CleanupConfig;
import de.nihas101.midas.core.sqlite.SqliteConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Data
@Configuration
@AllArgsConstructor
@ConfigurationProperties(prefix = "midas")
public class MidasConfig {
    private String title;
    private ThemeConfig theme;
    private UIConfig ui;
    private I18nConfig i18n;
    private DesktopConfig desktop;
    private CleanupConfig cleanup;
    private SqliteConfig sqlite;

    public MidasConfig() {
        this(
                "Midas",
                new ThemeConfig(),
                new UIConfig(),
                new I18nConfig(),
                new DesktopConfig(),
                new CleanupConfig(),
                new SqliteConfig()
        );
    }

    public MidasConfig(
            final ThemeConfig theme,
            final UIConfig ui,
            final I18nConfig i18n,
            final DesktopConfig desktop,
            final CleanupConfig cleanup,
            final SqliteConfig sqlite
    ) {
        this(
                "Midas",
                theme,
                ui,
                i18n,
                desktop,
                cleanup,
                sqlite
        );
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
