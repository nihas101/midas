package de.nihas101.midas.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TitleConfig {
    private final String name;

    public TitleConfig() {
        this("Midas");
    }
}
