package de.nihas101.midas.ui.interest;

import lombok.Getter;

// TODO: Upgrade to classes?
@Getter
public enum TransactionType {
    CREDIT("H"),
    DEBIT("S");

    private final String value;

    TransactionType(final String value) {
        this.value = value;
    }
}
