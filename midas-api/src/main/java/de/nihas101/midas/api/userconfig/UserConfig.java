package de.nihas101.midas.api.userconfig;

public interface UserConfig {

    int getId();

    String getUserIdentifier();

    String getTheme();

    String getLocale();

    void setLocale(String languageTag);

    void setTheme(String newTheme);
}
