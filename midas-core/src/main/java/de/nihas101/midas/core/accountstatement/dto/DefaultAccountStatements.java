package de.nihas101.midas.core.accountstatement.dto;

import de.nihas101.midas.api.accountstatement.AccountStatement;
import de.nihas101.midas.api.accountstatement.AccountStatements;
import de.nihas101.midas.api.accountstatement.LabeledAccountStatement;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverride;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@ToString
@EqualsAndHashCode
public class DefaultAccountStatements implements AccountStatements {

    private final Map<BookingType, LabeledAccountStatement> accountStatements;
    private final OpeningBalance openingBalance;
    private final Function<BookingType, LabeledAccountStatement> defaultsSupplier;
    private final List<LabeledAccountStatement> manualStatements;

    public DefaultAccountStatements(
            final Map<BookingType, LabeledAccountStatement> accountStatements,
            final OpeningBalance openingBalance,
            final Function<BookingType, LabeledAccountStatement> defaultsSupplier
    ) {
        this(
                accountStatements,
                openingBalance,
                defaultsSupplier,
                List.of()
        );
    }

    public DefaultAccountStatements(
            final Map<BookingType, LabeledAccountStatement> accountStatements,
            final OpeningBalance openingBalance,
            final Function<BookingType, LabeledAccountStatement> defaultsSupplier,
            final List<LabeledAccountStatement> manualStatements
    ) {
        this.accountStatements = accountStatements;
        this.openingBalance = openingBalance;
        this.defaultsSupplier = defaultsSupplier;
        this.manualStatements = manualStatements != null ? manualStatements : List.of();
    }

    public DefaultAccountStatements(
            final List<? extends AccountStatement> accountStatementEntities,
            final List<AccountStatementOverride> overrides,
            final Year year,
            final OpeningBalance openingBalance,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this(
                buildMergedStatements(
                        accountStatementEntities,
                        overrides,
                        year,
                        messageSource,
                        locale
                ),
                openingBalance,
                bookingType -> new DefaultAccountStatement(
                        null,
                        year,
                        bookingType,
                        MoneyAmount.ZERO,
                        messageSource,
                        locale
                ),
                buildManualStatements(overrides, year)
        );
    }

    private static Map<BookingType, LabeledAccountStatement> buildMergedStatements(
            final List<? extends AccountStatement> accountStatements,
            final List<AccountStatementOverride> overrides,
            final Year year,
            final MessageSource messageSource,
            final Locale locale
    ) {
        final Map<BookingType, AccountStatement> systemByType = Optional.ofNullable(accountStatements)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(AccountStatement::type, ase -> ase, (a, b) -> a));

        final Map<BookingType, AccountStatementOverride> overridesByType = Optional.ofNullable(overrides)
                .stream()
                .flatMap(Collection::stream)
                .filter(o -> o.getBookingType() != null)
                .collect(Collectors.toMap(AccountStatementOverride::getBookingType, o -> o, (a, b) -> a));

        final Map<BookingType, LabeledAccountStatement> merged = new EnumMap<>(BookingType.class);
        for (final BookingType type : BookingType.values()) {
            final AccountStatement systemEntity = systemByType.get(type);
            final AccountStatementOverride override = overridesByType.get(type);
            if (override != null) {
                final String label = messageSource != null
                        ? messageSource.getMessage(type.getAccountStatementI18nKey(), null, locale)
                        : type.name();

                final DefaultAccountStatement accountStatement = new DefaultAccountStatement(
                        override.getId(),
                        year,
                        type,
                        systemEntity != null ? systemEntity.amount() : override.getAmount(),
                        label,
                        override.getHidden(),
                        Source.SYSTEM
                );
                merged.put(
                        type,
                        accountStatement
                );
            } else {
                if (systemEntity != null) {
                    merged.put(type, new DefaultAccountStatement(systemEntity, messageSource, locale));
                } else {
                    merged.put(type, new DefaultAccountStatement(
                            null,
                            year,
                            type,
                            MoneyAmount.ZERO,
                            messageSource,
                            locale
                    ));
                }
            }
        }
        return merged;
    }

    private static List<LabeledAccountStatement> buildManualStatements(
            final List<AccountStatementOverride> overrides,
            final Year year
    ) {
        return Optional.ofNullable(overrides)
                .stream()
                .flatMap(Collection::stream)
                .filter(o -> o.getBookingType() == null)
                .map(o -> (LabeledAccountStatement) new ManualAccountStatement(
                                o.getId(),
                                year,
                                o.getAmount(),
                                o.getLabelOverride(),
                                false
                        )
                )
                .toList();
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

    @Override
    public List<LabeledAccountStatement> manualStatements() {
        return manualStatements;
    }
}
