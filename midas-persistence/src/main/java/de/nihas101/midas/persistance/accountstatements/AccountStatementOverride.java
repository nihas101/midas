package de.nihas101.midas.persistance.accountstatements;

import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;

public interface AccountStatementOverride {
    Integer getId();

    Integer getShareholderId();

    Integer getYear();

    BookingType getBookingType();

    String getLabel();

    Boolean getHidden();

    MoneyAmount getAmount();
}
