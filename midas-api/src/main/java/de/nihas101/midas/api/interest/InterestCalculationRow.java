package de.nihas101.midas.api.interest;

import java.math.BigDecimal;

public interface InterestCalculationRow {

    String label();

    Transaction totalTransaction();

    Transaction balanceAtEndOfMonth();

    BigDecimal interestDaysCount();

    BigDecimal interestAmount();

    default String partName() {
        return "no-separator-column";
    }
}
