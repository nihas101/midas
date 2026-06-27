package de.nihas101.midas.accountstatement.row;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;

public interface AccountStatementRow {
    Integer displayId();

    String dateStr();

    String label();

    MoneyAmount debit();

    MoneyAmount credit();

    MoneyAmount balance();

    default MoneyAmount amount() {
        return MoneyAmount.ZERO;
    }


    default String partName() {
        if (isHidden()) {
            return "hidden no-separator-column";
        }

        if (isManualExtra()) {
            return "manual-extra no-separator-column";
        }
        return "no-separator-column";
    }

    default boolean isOpeningBalance() {
        return false;
    }

    default boolean isHidden() {
        return false;
    }

    default boolean isManualExtra() {
        return false;
    }

    default BookingType bookingType() {
        return null;
    }

    default String rowKey() {
        if (bookingType() != null) {
            return "TYPE:" + bookingType().name();
        } else {
            return "MANUAL:" + displayId();
        }
    }
}
