package de.nihas101.midas.core.interest.row;

import lombok.Getter;

@Getter
public enum TransactionType {
    CREDIT("H"),
    DEBIT("S");

    private final String value;

    TransactionType(final String value) {
        this.value = value;
    }
}
