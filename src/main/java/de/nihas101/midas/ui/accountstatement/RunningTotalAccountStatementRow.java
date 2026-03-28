package de.nihas101.midas.ui.accountstatement;

import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatement;
import de.nihas101.midas.money.MoneyAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RequiredArgsConstructor
public class RunningTotalAccountStatementRow implements AccountStatementRow {

    private final RunningTotalAccountStatement accountStatement;

    @Override
    public Integer displayId() {
        return accountStatement.id();
    }

    @Override
    public String dateStr() {
        final LocalDate date = accountStatement.year().atMonth(Month.DECEMBER).atEndOfMonth();
        return date.format(DateTimeFormatter.ofPattern("dd.MM")); // TODO: Make this configurable
    }

    @Override
    public String label(final MessageSource messageSource, final Locale locale) {
        return accountStatement.label(messageSource, locale);
    }

    @Override
    public MoneyAmount debit() {
        final MoneyAmount amount = accountStatement.amount();
        return amount.smallerThan(MoneyAmount.ZERO) ? amount : null;
    }

    @Override
    public MoneyAmount credit() {
        final MoneyAmount amount = accountStatement.amount();
        return amount.smallerThan(MoneyAmount.ZERO) ? null : amount;
    }

    @Override
    public MoneyAmount balance() {
        return accountStatement.currentBalance();
    }
}
