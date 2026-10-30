package de.nihas101.midas.core.config;

import de.nihas101.midas.core.cleanup.CleanupConfig;
import de.nihas101.midas.persistance.DbConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Data
@Component
@RequiredArgsConstructor
public class CoreConfig {
    private final TitleConfig title;
    private final ThemeConfig theme;
    private final UIConfig ui;
    private final I18nConfig i18n;
    private final CleanupConfig cleanup;
    private final DbConfig db;
    private final DatesConfig dates;
    private final ShareholderConfig shareholder;
    private final ActiveBookingPeriod activeBookingPeriod;

    @Autowired
    public CoreConfig(
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
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    public LocalDate cleanupStart() {
        return activeBookingPeriod.cleanupStart();
    }

}
