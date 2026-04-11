package de.nihas101.midas.ui.accountstatement;

import de.nihas101.midas.money.MoneyAmount;
import org.springframework.context.MessageSource;

import java.util.Locale;

public interface AccountStatementRow {
    Integer displayId();

    String dateStr();

    String label(final MessageSource messageSource, final Locale locale);

    MoneyAmount debit();

    MoneyAmount credit();

    MoneyAmount balance();

    default String partName() {
        return "no-separator-column";
    }
}
