package de.nihas101.midas.api.bookings;

import de.nihas101.midas.api.money.MoneyAmount;

import java.time.LocalDate;

public interface Booking {

    Integer getId();

    Integer getDisplayId();

    Integer getShareholderId();

    LocalDate getDate();

    BookingType getType();

    MoneyAmount getAmount();

    String getComment();

    Source getSource();

    void setId(final int id);

    void setShareholderId(final Integer shareholderId);

    void setDate(final LocalDate date);

    void setType(final BookingType bookingType);

    void setAmount(final MoneyAmount amount);

    void setComment(final String comment);

    void setSource(final Source source);
}
