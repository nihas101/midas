package de.nihas101.midas.headless.config;

import de.nihas101.midas.core.cleanup.CleanupConfig;
import de.nihas101.midas.core.config.DesktopConfig;
import de.nihas101.midas.core.config.I18nConfig;
import de.nihas101.midas.core.config.ThemeConfig;
import de.nihas101.midas.core.config.TitleConfig;
import de.nihas101.midas.core.config.UIConfig;
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
    private TitleConfig title;
    private ThemeConfig theme;
    private UIConfig ui;
    private I18nConfig i18n;
    private CleanupConfig cleanup;
    private SqliteConfig sqlite;

    public MidasConfig() {
        this(
                new TitleConfig(),
                new ThemeConfig(),
                new UIConfig(),
                new I18nConfig(),
                new CleanupConfig(),
                new SqliteConfig()
        );
    }

    @Bean
    public TitleConfig titleConfig() {
        return title;
    }

    @Bean
    public ThemeConfig themeConfig() {
        return theme;
    }

    @Bean
    public UIConfig uiConfig() {
        return ui;
    }

    @Bean
    public I18nConfig i18nConfig() {
        return i18n;
    }

    @Bean
    public CleanupConfig cleanupConfig() {
        return cleanup;
    }

    @Bean
    public SqliteConfig sqliteConfig() {
        return sqlite;
    }

}
