package de.nihas101.midas.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UIConfig {
    private boolean hideThemeToggle;
    boolean hideLanguageSelector;

    public UIConfig() {
        this(false, false);
    }
}
