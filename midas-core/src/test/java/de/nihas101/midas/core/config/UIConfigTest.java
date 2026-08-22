package de.nihas101.midas.core.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UIConfigTest {

    @Test
    void defaultValues() {
        final UIConfig config = new UIConfig();
        assertFalse(config.isHideThemeToggle());
        assertFalse(config.isHideLanguageSelector());
        assertFalse(config.isDefaultAddAnotherCheckboxState());
        assertEquals("€", config.getCurrencySymbol());
    }

    @Test
    void customValues() {
        final UIConfig config = new UIConfig(true, true, true, "$");
        assertEquals("$", config.getCurrencySymbol());
    }
}
