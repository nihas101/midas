package de.nihas101.midas.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Data
@Configuration
@ConfigurationProperties(prefix = "midas")
public class MidasConfig {
    private ThemeConfig theme = new ThemeConfig();
    private UIConfig ui = new UIConfig();
    private I18nConfig i18n = new I18nConfig();

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
