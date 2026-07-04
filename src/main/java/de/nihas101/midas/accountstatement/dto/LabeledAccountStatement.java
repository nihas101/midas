package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.bookings.entity.BookingType;

public interface LabeledAccountStatement extends AccountStatement {

    String label();

    boolean isManualExtra();

    default BookingType bookingType() {
        return null;
    }

    boolean isHidden();

    default String rowKey() {
        return new RowKey(bookingType(), id()).toString();
    }
}
