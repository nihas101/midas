package de.nihas101.midas.api.accountstatement;

import de.nihas101.midas.commons.BookingType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RowKey {

    private final BookingType bookingType;
    private final Integer id;

    @Override
    public String toString() {
        if (bookingType != null) {
            return "TYPE:" + bookingType.name();
        } else {
            return "MANUAL:" + id;
        }
    }
}
