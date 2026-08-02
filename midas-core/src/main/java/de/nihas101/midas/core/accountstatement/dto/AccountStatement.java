package de.nihas101.midas.core.accountstatement.dto;

import de.nihas101.midas.core.money.MoneyAmount;

import java.time.LocalDate;

public interface AccountStatement {
    Integer id();

    LocalDate date();

    MoneyAmount amount();
}
