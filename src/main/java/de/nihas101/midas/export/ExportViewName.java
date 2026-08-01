package de.nihas101.midas.export;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExportViewName {
    BOOKINGS("bookings"),
    ACCOUNT_STATEMENTS("account-statements"),
    INTEREST("interest");

    private final String name;
}
