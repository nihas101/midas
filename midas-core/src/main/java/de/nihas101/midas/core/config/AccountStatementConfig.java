package de.nihas101.midas.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountStatementConfig {
    public static final String DEFAULT_DATE_FORMAT = "dd.MM";
    private String dateFormat;

    public AccountStatementConfig() {
        this(DEFAULT_DATE_FORMAT);
    }
}
