package de.nihas101.midas.api.openingbalance;

import de.nihas101.midas.api.bookings.Source;
import de.nihas101.midas.api.money.MoneyAmount;

import java.time.Year;

public interface OpeningBalance {

    Integer getId();

    Integer getShareholderId();

    MoneyAmount getOpeningBalance();

    Year getYear();

    Source getSource();

    void setId(final Integer id);

    void setShareholderId(final Integer shareholderId);

    void setOpeningBalance(final MoneyAmount openingBalance);

    void setYear(final Year year);

    void setSource(final Source source);
}
