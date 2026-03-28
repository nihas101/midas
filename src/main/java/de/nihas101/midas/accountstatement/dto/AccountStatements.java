package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.accountstatement.repository.AccountStatementEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AccountStatements {

    private final Map<BookingType, AccountStatement> accountStatements;
    private final Year year;

    public static AccountStatements fromEntity(final List<AccountStatementEntity> accountStatementEntities, final Year year) {
        final Map<BookingType, AccountStatement> monthlyStatements = accountStatementEntities.stream()
                .collect(
                        Collectors.toMap(
                                AccountStatementEntity::getType,
                                AccountStatement::new
                        )
                );
        return new AccountStatements(monthlyStatements, year);
    }

    public AccountStatement forType(final BookingType bookingType) {
        return accountStatements.getOrDefault(
                bookingType,
                new AccountStatement(
                        null,
                        year,
                        bookingType,
                        MoneyAmount.ZERO
                )
        );
    }
}
