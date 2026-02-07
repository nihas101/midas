package de.nihas101.midas.config;

import lombok.Data;

@Data
public class I18nConfig {
    String defaultLocale = "en";
    boolean forceDefaultLanguage = false;
}
