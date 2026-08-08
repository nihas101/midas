package de.nihas101.midas.api.interest;

import java.math.BigDecimal;

public interface Interest {
    // aka 'Tageszins' Z
    BigDecimal dailyInterestRate();

    // aka 'Zins-' or 'Diskontzahl'
    BigDecimal interestAmount();

    // aka 'Zinsteiler'
    BigDecimal interestDivisor();

    BigDecimal interestDays();
}
