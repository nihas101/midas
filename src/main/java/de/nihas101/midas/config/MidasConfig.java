package de.nihas101.midas.config;

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
    private ThemeConfig theme;
    private UIConfig ui;
    private I18nConfig i18n;
    private DesktopConfig desktop;

    public MidasConfig() {
        this(
                new ThemeConfig(),
                new UIConfig(),
                new I18nConfig(),
                new DesktopConfig()
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
