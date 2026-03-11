package de.nihas101.midas.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class I18nConfig {
    String defaultLocale;
    boolean forceDefaultLanguage;

    public I18nConfig() {
        this("en", false);
    }
}
