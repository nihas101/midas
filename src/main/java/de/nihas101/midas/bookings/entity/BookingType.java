package de.nihas101.midas.bookings.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BookingType {
    // TODO: Convert to english
    ENTNAHME(1, "bookings.type.entnahme"),
    STEUERN_VJ(2, "bookings.type.steuern_vj"),
    STEUERN_KR(3, "bookings.type.steuern_kr"),
    ZINSEN(4, "bookings.type.zinsen"),
    VERGUETUNG(5, "bookings.type.verguetung"),
    SALDOVORTRAG(31, "bookings.type.saldovortrag");

    private final int id;
    private final String i18nKey;

    public static BookingType fromId(int id) {
        return Arrays.stream(values())
                .filter(t -> t.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown BookingType ID: " + id));
    }
}
