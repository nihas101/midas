package de.nihas101.midas.bookings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum BookingType {
    WITHDRAWAL(
            1,
            1,
            "bookings.type.withdrawal",
            "bookings.type.account-summary.withdrawal"
    ),
    TAX_PREVIOUS_YEAR(
            2,
            2,
            "bookings.type.tax-previous-year",
            "bookings.type.account-summary.tax-previous-year"
    ),
    TAX_CREDIT(
            3,
            3,
            "bookings.type.tax-credit",
            "bookings.type.account-summary.tax-credit"
    ),
    INTEREST(
            4,
            5,
            "bookings.type.interest",
            "bookings.type.account-summary.interest"
    ),
    COMPENSATION(
            5,
            4,
            "bookings.type.compensation",
            "bookings.type.account-summary.compensation"
    );

    private final int id;
    private final int sortKey;
    private final String i18nKey;
    private final String accountStatementI18nKey;

    public static BookingType fromId(int id) {
        return Arrays.stream(values())
                .filter(t -> t.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown BookingType ID: " + id));
    }

    public static List<BookingType> creatableByUser() {
        return Arrays.stream(BookingType.values())
                .toList();
    }
}
