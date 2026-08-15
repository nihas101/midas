package de.nihas101.midas.vaadin.config;

import de.nihas101.midas.core.cleanup.CleanupConfig;
import de.nihas101.midas.core.config.AccountStatementConfig;
import de.nihas101.midas.core.config.I18nConfig;
import de.nihas101.midas.core.config.ThemeConfig;
import de.nihas101.midas.core.config.TitleConfig;
import de.nihas101.midas.core.config.UIConfig;
import de.nihas101.midas.persistance.DbConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@AllArgsConstructor
@ConfigurationProperties(prefix = "midas")
public class MidasConfig {
    private TitleConfig title;
    private ThemeConfig theme;
    private UIConfig ui;
    private I18nConfig i18n;
    private DesktopConfig desktop;
    private CleanupConfig cleanup;
    private DbConfig db;
    private AccountStatementConfig accountStatement;

    public MidasConfig() {
        this(
                new TitleConfig(),
                new ThemeConfig(),
                new UIConfig(),
                new I18nConfig(),
                new DesktopConfig(),
                new CleanupConfig(),
                new DbConfig() {
                },
                new AccountStatementConfig()
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
    public DesktopConfig desktopConfig() {
        return desktop;
    }

    @Bean
    public CleanupConfig cleanupConfig() {
        return cleanup;
    }

    @Bean
    public DbConfig dbConfig() {
        return db;
    }

    @Bean
    public AccountStatementConfig accountStatementConfig() {
        return accountStatement;
    }
}
