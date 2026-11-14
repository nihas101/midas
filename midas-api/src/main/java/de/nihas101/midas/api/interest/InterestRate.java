package de.nihas101.midas.api.interest;

import java.math.BigDecimal;
import java.time.Year;

public interface InterestRate {
    Integer getId();

    Integer getShareholderId();

    BigDecimal getInterestRate();

    Year getYear();

    void setId(Integer id);

    void setShareholderId(final Integer shareholderId);

    void setInterestRate(final BigDecimal interestRate);

    void setYear(final Year year);
}
