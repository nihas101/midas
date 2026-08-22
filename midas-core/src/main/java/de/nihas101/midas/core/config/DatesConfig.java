package de.nihas101.midas.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DatesConfig {
    private String mediumDateFormat;
    private String longDateFormat;

    public DatesConfig() {
        this(
                "dd.MM",
                "dd.MM.yyyy"
        );
    }
}
