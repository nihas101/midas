package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.accountstatement.repository.AccountStatementEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;

import java.time.Year;

public record AccountStatement(
        Integer id,
        Year year,
        BookingType type,
        MoneyAmount amount
) {

    public AccountStatement(final AccountStatementEntity accountStatementEntity) {
        this(
                accountStatementEntity.getId(),
                Year.of(accountStatementEntity.getDate().getYear()),
                accountStatementEntity.getType(),
                accountStatementEntity.getAmount()
        );
    }
}
