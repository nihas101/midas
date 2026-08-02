package de.nihas101.midas.api.accountstatement;

import de.nihas101.midas.api.money.MoneyAmount;

import java.time.LocalDate;

public interface AccountStatement {
    Integer id();

    LocalDate date();

    MoneyAmount amount();
}
