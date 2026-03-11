package de.nihas101.midas.bookings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BookingType {
    WITHDRAWAL(1, "bookings.type.withdrawal"),
    TAX_PREVIOUS_YEAR(2, "bookings.type.tax-previous-year"),
    TAX_CREDIT(3, "bookings.type.tax-credit"),
    INTEREST(4, "bookings.type.interest"),
    COMPENSATION(5, "bookings.type.compensation"),
    OPENING_BALANCE(31, "bookings.type.opening-balance");

    private final int id;
    private final String i18nKey;

    public static BookingType fromId(int id) {
        return Arrays.stream(values())
                .filter(t -> t.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown BookingType ID: " + id)); // TODO: i18n
    }
}
