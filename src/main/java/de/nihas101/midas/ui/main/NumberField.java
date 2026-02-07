package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.textfield.IntegerField;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class NumberField extends IntegerField {

    public NumberField(MessageSource messageSource, final Locale locale) {
        setLabel(messageSource.getMessage("number.field.label", null, locale));
        setPlaceholder(messageSource.getMessage("number.field.placeholder", null, locale));
    }
}
