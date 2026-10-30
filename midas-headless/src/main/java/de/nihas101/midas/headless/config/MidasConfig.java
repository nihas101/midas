package de.nihas101.midas.headless.config;

import de.nihas101.midas.core.cleanup.CleanupConfig;
import de.nihas101.midas.core.config.ActiveBookingPeriod;
import de.nihas101.midas.core.config.DatesConfig;
import de.nihas101.midas.core.config.I18nConfig;
import de.nihas101.midas.core.config.ShareholderConfig;
import de.nihas101.midas.core.config.ThemeConfig;
import de.nihas101.midas.core.config.TitleConfig;
import de.nihas101.midas.core.config.UIConfig;
import de.nihas101.midas.persistance.DbConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

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
    private DbConfig db;
    private DatesConfig dates;
    private ShareholderConfig shareholder;
    private ActiveBookingPeriod activeBookingPeriod;

    public MidasConfig() {
        this(new CleanupConfig());
    }

    private MidasConfig(final CleanupConfig cleanupConfig) {
        this(
                new TitleConfig(),
                new ThemeConfig(),
                new UIConfig(),
                new I18nConfig(),
                cleanupConfig,
                new DbConfig() {
                },
                new DatesConfig(),
                new ShareholderConfig(),
                new ActiveBookingPeriod(cleanupConfig)
        );
    }

    public MidasConfig(
            final TitleConfig title,
            final ThemeConfig theme,
            final UIConfig ui,
            final I18nConfig i18n,
            final CleanupConfig cleanup,
            final DbConfig db,
            final DatesConfig dates,
            final ShareholderConfig shareholder
    ) {
        this(
                title,
                theme,
                ui,
                i18n,
                cleanup,
                db,
                dates,
                shareholder,
                new ActiveBookingPeriod(cleanup)
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
    public DbConfig dbConfig() {
        return db;
    }

    @Bean
    public DatesConfig datesConfig() {
        return dates;
    }

    @Bean
    public ShareholderConfig shareholderConfig() {
        return shareholder;
    }

    @Bean
    public ActiveBookingPeriod activeBookingPeriod() {
        return activeBookingPeriod;
    }

    public LocalDate bookingActivePeriodStart() {
        return activeBookingPeriod.start();
    }

    public LocalDate bookingActivePeriodEnd() {
        return activeBookingPeriod.end();
    }
}
