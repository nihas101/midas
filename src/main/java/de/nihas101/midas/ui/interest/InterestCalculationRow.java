package de.nihas101.midas.ui.interest;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;

// TODO: Break this interface down? (or at least rename it to something more clear -> Used to carry info about booking that will be displayed in a row)
// TODO: It would probably be better to not expose the fields and instead just have the row consume a grid and add itself (same with bookingrow)
public interface InterestCalculationRow {

    String monthAsString();

    String totalTransactionAmountAsString();

    String balanceAtEndOfMonth();

    int interestDaysCount();

    int interestAmount(); // TODO: Is this the correct term?

}
