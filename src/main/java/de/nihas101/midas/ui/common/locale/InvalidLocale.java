package de.nihas101.midas.ui.common.locale;

import java.util.Locale;

public class InvalidLocale {

    private static final Locale INVALID_LOCALE = Locale.of("");

    public boolean corresponds(final Object obj) {
        return obj == null || INVALID_LOCALE.equals(obj);
    }

    public boolean corresponds(final InvalidLocale ignored) {
        return true;
    }
}
