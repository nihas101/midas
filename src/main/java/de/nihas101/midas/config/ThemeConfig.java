package de.nihas101.midas.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ThemeConfig {
    private String defaultTheme = "";

    public ThemeConfig() {
        this("");
    }
}
