package de.nihas101.midas.api.interest;

import java.math.BigDecimal;

// TODO: Break this interface down? (or at least rename it to something more clear -> Used to carry info about booking that will be displayed in a row)
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
