package de.nihas101.midas.accountstatement.dto;

import de.nihas101.midas.money.MoneyAmount;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.util.Locale;

public interface AccountStatement {
    Integer id();

    LocalDate date();

    String label(final MessageSource messageSource, final Locale locale);

    MoneyAmount amount();
}
