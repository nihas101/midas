package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.accountstatement.repository.AccountStatementEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Year;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class DefaultAccountStatements implements AccountStatements {

    private final Map<BookingType, AccountStatement> accountStatements;
    private final OpeningBalance openingBalance;
    private final Function<BookingType, AccountStatement> defaultsSupplier;

    public DefaultAccountStatements(
            final List<AccountStatementEntity> accountStatementEntities, // TODO: Map to DTO beforehand!
            final Year year,
            final OpeningBalance openingBalance
    ) {
        this(
                accountStatementEntities,
                openingBalance,
                bookingType -> new DefaultAccountStatement(
                        null,
                        year,
                        bookingType,
                        MoneyAmount.ZERO
                )

        );
    }

    public DefaultAccountStatements(
            final List<AccountStatementEntity> accountStatementEntities, // TODO: Map to DTO beforehand!
            final OpeningBalance openingBalance,
            final Function<BookingType, AccountStatement> defaultsSupplier
    ) {
        this(
                Optional.ofNullable(accountStatementEntities)
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(
                                Collectors.toMap(
                                        AccountStatementEntity::getType,
                                        DefaultAccountStatement::new,
                                        DefaultAccountStatements::firstAccountStatement
                                )
                        ),
                openingBalance,
                defaultsSupplier
        );
    }

    private static AccountStatement firstAccountStatement(
            final AccountStatement first,
            final AccountStatement second
    ) {
        return first;
    }

    @Override
    public OpeningBalance openingBalance() {
        return openingBalance;
    }

    @Override
    public AccountStatement forType(final BookingType bookingType) {
        if (bookingType == null) {
            return null;
        }
        final AccountStatement defaultValue = defaultsSupplier != null ? defaultsSupplier.apply(bookingType) : null;
        if (accountStatements == null) {
            return defaultValue;
        }

        return accountStatements.getOrDefault(bookingType, defaultValue);
    }
}
