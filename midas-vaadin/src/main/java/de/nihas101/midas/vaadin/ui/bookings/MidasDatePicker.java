package de.nihas101.midas.vaadin.ui.bookings;

import com.vaadin.flow.component.datepicker.DatePicker;
import de.nihas101.midas.vaadin.config.MidasConfig;

import java.time.LocalDate;
import java.util.Locale;

public class MidasDatePicker extends DatePicker {

    public MidasDatePicker(
            final MidasConfig midasConfig,
            final Locale locale,
            final DatePicker.DatePickerI18n i18n,
            final String label
    ) {
        super(label);
        this.setLocale(locale);
        this.setMin(LocalDate.now().minus(midasConfig.getCleanup().getCutoff()));
        this.setI18n(i18n);
        this.setRequired(true);
    }
}