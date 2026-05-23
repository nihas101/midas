package de.nihas101.midas.accountstatement.row;

import de.nihas101.midas.money.MoneyAmount;

public interface AccountStatementRow {
    Integer displayId();

    String dateStr();

    String label();

    MoneyAmount debit();

    MoneyAmount credit();

    MoneyAmount balance();

    default String partName() {
        return "no-separator-column";
    }
}
