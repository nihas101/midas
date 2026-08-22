package de.nihas101.midas.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UIConfig {
    private boolean hideThemeToggle;
    private boolean hideLanguageSelector;
    private boolean defaultAddAnotherCheckboxState;
    private String currencySymbol;

    public UIConfig() {
        this(false, false, false, "€");
    }
}
