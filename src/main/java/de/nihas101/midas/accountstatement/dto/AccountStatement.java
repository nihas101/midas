package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.money.MoneyAmount;

import java.time.LocalDate;

public interface AccountStatement {
    Integer id();

    LocalDate date();

    MoneyAmount amount();
}
