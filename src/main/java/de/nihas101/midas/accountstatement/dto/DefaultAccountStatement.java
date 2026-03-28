package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.accountstatement.repository.AccountStatementEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.Locale;

@RequiredArgsConstructor
public final class DefaultAccountStatement implements AccountStatement {
    private final Integer id;
    private final Year year;
    private final BookingType type;
    private final MoneyAmount amount;

    public DefaultAccountStatement(final AccountStatementEntity accountStatementEntity) {
        this(
                accountStatementEntity.getId(),
                Year.of(accountStatementEntity.getDate().getYear()),
                accountStatementEntity.getType(),
                accountStatementEntity.getAmount()
        );
    }

    @Override
    public Integer id() {
        return id;
    }

    @Override
    public Year year() {
        return year;
    }

    @Override
    public String label(final MessageSource messageSource, final Locale locale) {
        return messageSource.getMessage(type.getAccountStatementI18nKey(), null, locale);
    }

    @Override
    public MoneyAmount amount() {
        return amount;
    }

}
