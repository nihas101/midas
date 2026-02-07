package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.IntegerField;
import de.nihas101.midas.example.service.NumberWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.util.Locale;

@Slf4j
public class AddNumberButton extends Button {

    private final NumberWriter numberWriter;
    private final IntegerField numberField;

    protected AddNumberButton(
            final IntegerField numberField,
            final NumberWriter numberWriter,
            final MessageSource messageSource,
            final Locale locale
    ) {
        super(messageSource.getMessage("add.number.button", null, locale));
        this.numberField = numberField;
        this.numberWriter = numberWriter;
        this.addClickListener(this::addNumber);
    }

    private void addNumber(ClickEvent<Button> ignored) {
        Integer value = numberField.getValue();
        if (value == null) {
            return;
        }
        numberWriter.addNumber(value);
        numberField.clear();
    }

}
