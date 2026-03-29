package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.accountstatement.repository.AccountStatementEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Locale;
import java.util.Optional;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class DefaultAccountStatement implements AccountStatement {
    private final Integer id;
    private final Year year;
    private final BookingType type;
    private final MoneyAmount amount;

    public DefaultAccountStatement(final AccountStatementEntity accountStatementEntity) {
        this(
                accountStatementEntity != null ? accountStatementEntity.getId() : null,
                Optional.ofNullable(accountStatementEntity)
                        .map(AccountStatementEntity::getDate)
                        .map(LocalDate::getYear)
                        .map(Year::of)
                        .orElse(null),
                accountStatementEntity != null ? accountStatementEntity.getType() : null,
                accountStatementEntity != null ? accountStatementEntity.getAmount() : null
        );
    }

    @Override
    public Integer id() {
        return id;
    }

    @Override
    public LocalDate date() {
        return year != null ? year.atMonth(Month.DECEMBER).atEndOfMonth() : null;
    }

    @Override
    public String label(final MessageSource messageSource, final Locale locale) {
        if (messageSource == null || locale == null) {
            return null;
        }
        return messageSource.getMessage(type.getAccountStatementI18nKey(), null, locale);
    }

    @Override
    public MoneyAmount amount() {
        return amount;
    }

}
