package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.datepicker.DatePicker;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.Locale;

public class DatePickerI18nProvider { // TODO: Clean up and let spring boot handle the creation and injection

    public static final int MONDAY = 1;

    public static DatePicker.DatePickerI18n datePickerI18n(MessageSource messageSource, Locale locale) {
        DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();

        i18n.setMonthNames(Arrays.asList(
                messageSource.getMessage("datepicker.month.january", null, locale),
                messageSource.getMessage("datepicker.month.february", null, locale),
                messageSource.getMessage("datepicker.month.march", null, locale),
                messageSource.getMessage("datepicker.month.april", null, locale),
                messageSource.getMessage("datepicker.month.may", null, locale),
                messageSource.getMessage("datepicker.month.june", null, locale),
                messageSource.getMessage("datepicker.month.july", null, locale),
                messageSource.getMessage("datepicker.month.august", null, locale),
                messageSource.getMessage("datepicker.month.september", null, locale),
                messageSource.getMessage("datepicker.month.october", null, locale),
                messageSource.getMessage("datepicker.month.november", null, locale),
                messageSource.getMessage("datepicker.month.december", null, locale)
        ));

        i18n.setWeekdays(Arrays.asList(
                messageSource.getMessage("datepicker.day.sunday", null, locale),
                messageSource.getMessage("datepicker.day.monday", null, locale),
                messageSource.getMessage("datepicker.day.tuesday", null, locale),
                messageSource.getMessage("datepicker.day.wednesday", null, locale),
                messageSource.getMessage("datepicker.day.thursday", null, locale),
                messageSource.getMessage("datepicker.day.friday", null, locale),
                messageSource.getMessage("datepicker.day.saturday", null, locale)
        ));

        i18n.setWeekdaysShort(Arrays.asList(
                messageSource.getMessage("datepicker.day.short.sunday", null, locale),
                messageSource.getMessage("datepicker.day.short.monday", null, locale),
                messageSource.getMessage("datepicker.day.short.tuesday", null, locale),
                messageSource.getMessage("datepicker.day.short.wednesday", null, locale),
                messageSource.getMessage("datepicker.day.short.thursday", null, locale),
                messageSource.getMessage("datepicker.day.short.friday", null, locale),
                messageSource.getMessage("datepicker.day.short.saturday", null, locale)
        ));

        i18n.setToday(messageSource.getMessage("datepicker.today", null, locale));
        i18n.setCancel(messageSource.getMessage("datepicker.cancel", null, locale));
        i18n.setFirstDayOfWeek(MONDAY);

        return i18n;
    }
}
