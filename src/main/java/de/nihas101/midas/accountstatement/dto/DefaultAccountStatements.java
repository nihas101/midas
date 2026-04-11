package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.accountstatement.repository.AccountStatementEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class DefaultAccountStatements implements AccountStatements {

    private final Map<BookingType, LabeledAccountStatement> accountStatements;
    private final OpeningBalance openingBalance;
    private final Function<BookingType, LabeledAccountStatement> defaultsSupplier;

    public DefaultAccountStatements(
            final List<AccountStatementEntity> accountStatementEntities, // TODO: Map to DTO beforehand!
            final Year year,
            final OpeningBalance openingBalance,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                accountStatementEntities,
                openingBalance,
                bookingType -> new DefaultAccountStatement(
                        null,
                        year,
                        bookingType,
                        MoneyAmount.ZERO,
                        messageSource,
                        locale
                ),
                messageSource,
                locale
        );
    }

    public DefaultAccountStatements(
            final List<AccountStatementEntity> accountStatementEntities, // TODO: Map to DTO beforehand!
            final OpeningBalance openingBalance,
            final Function<BookingType, LabeledAccountStatement> defaultsSupplier,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                Optional.ofNullable(accountStatementEntities)
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(
                                Collectors.toMap(
                                        AccountStatementEntity::getType,
                                        ase -> new DefaultAccountStatement(ase, messageSource, locale),
                                        DefaultAccountStatements::firstAccountStatement
                                )
                        ),
                openingBalance,
                defaultsSupplier
        );
    }

    private static LabeledAccountStatement firstAccountStatement(
            final LabeledAccountStatement first,
            final LabeledAccountStatement second
    ) {
        return first;
    }

    @Override
    public OpeningBalance openingBalance() {
        return openingBalance;
    }

    @Override
    public LabeledAccountStatement forType(final BookingType bookingType) {
        if (bookingType == null) {
            return null;
        }
        final LabeledAccountStatement defaultValue = defaultsSupplier != null ? defaultsSupplier.apply(bookingType) : null;
        if (accountStatements == null) {
            return defaultValue;
        }

        return accountStatements.getOrDefault(bookingType, defaultValue);
    }
}
