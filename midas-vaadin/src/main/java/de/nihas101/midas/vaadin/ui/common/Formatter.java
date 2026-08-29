package de.nihas101.midas.vaadin.ui.common;

import de.nihas101.midas.commons.MoneyAmount;

import java.math.BigDecimal;
import java.util.Locale;

public class Formatter {
    private final Locale locale;

    public Formatter(final Locale locale) {
        this.locale = locale;
    }

    public String formatInterestAmounts(final BigDecimal interestAmounts) {
        // To display empty cells for empty amounts
        return interestAmounts == null ? "" : interestAmounts.toString();
    }

    public String formatDays(final BigDecimal days) {
        // To display empty cells for empty amounts
        return days == null ? "" : String.valueOf(days.intValue());
    }

    public String formatAmount(final MoneyAmount amount) {
        // To display empty cells for empty amounts
        return amount == null || amount.equals(MoneyAmount.ZERO)
                ? ""
                : amount.format(locale);
    }
}