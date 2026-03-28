package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.accountstatement.repository.AccountStatementEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import lombok.RequiredArgsConstructor;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultAccountStatements implements AccountStatements {

    private final Map<BookingType, DefaultAccountStatement> accountStatements;
    private final Year year;
    private final OpeningBalance openingBalance;

    // TODO: Turn into constructor
    public static DefaultAccountStatements fromEntity(
            final List<AccountStatementEntity> accountStatementEntities,
            final Year year,
            final OpeningBalance openingBalance
    ) {
        return new DefaultAccountStatements(
                accountStatementEntities.stream()
                        .collect(
                                Collectors.toMap(
                                        AccountStatementEntity::getType,
                                        DefaultAccountStatement::new
                                )
                        ),
                year,
                openingBalance
        );
    }

    @Override
    public OpeningBalance openingBalance() {
        return openingBalance;
    }

    @Override
    public DefaultAccountStatement forType(final BookingType bookingType) {
        return accountStatements.getOrDefault(
                bookingType,
                new DefaultAccountStatement(
                        null,
                        year,
                        bookingType,
                        MoneyAmount.ZERO
                )
        );
    }
}
