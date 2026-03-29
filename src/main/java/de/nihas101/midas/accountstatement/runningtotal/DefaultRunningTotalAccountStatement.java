package de.nihas101.midas.accountstatement.runningtotal;

import de.nihas101.midas.accountstatement.dto.AccountStatement;
import de.nihas101.midas.money.MoneyAmount;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.util.Locale;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class DefaultRunningTotalAccountStatement implements RunningTotalAccountStatement {

    private final AccountStatement statement;
    private final MoneyAmount currentBalance;

    @Override
    public Integer id() {
        return statement != null ? statement.id() : null;
    }

    @Override
    public LocalDate date() {
        return statement != null ? statement.date() : null;
    }

    @Override
    public String label(final MessageSource messageSource, final Locale locale) {
        return statement != null ? statement.label(messageSource, locale) : null;
    }

    @Override
    public MoneyAmount amount() {
        return statement != null ? statement.amount() : null;
    }

    @Override
    public MoneyAmount currentBalance() {
        return statement != null ? currentBalance : null;
    }
}
