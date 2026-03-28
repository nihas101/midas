package de.nihas101.midas.accountstatement.runningtotal;

import de.nihas101.midas.accountstatement.dto.AccountStatement;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.util.Locale;

@RequiredArgsConstructor
public class DefaultRunningTotalAccountStatement implements RunningTotalAccountStatement {

    private final AccountStatement statement;
    private final MoneyAmount currentBalance;

    @Override
    public Integer id() {
        return statement.id();
    }

    @Override
    public LocalDate date() {
        return statement.date();
    }

    @Override
    public String label(final MessageSource messageSource, final Locale locale) {
        return statement.label(messageSource, locale);
    }

    @Override
    public MoneyAmount amount() {
        return statement.amount();
    }

    @Override
    public MoneyAmount currentBalance() {
        return currentBalance;
    }
}
