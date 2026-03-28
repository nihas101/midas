package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.money.MoneyAmount;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.Locale;

public interface AccountStatement {
    Integer id();

    Year year();

    String label(final MessageSource messageSource, final Locale locale);

    MoneyAmount amount();
}
