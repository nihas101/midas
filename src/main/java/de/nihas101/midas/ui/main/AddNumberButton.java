package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.IntegerField;
import de.nihas101.midas.example.service.NumberWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddNumberButton extends Button {

    private final NumberWriter numberWriter;
    private final IntegerField numberField;

    public AddNumberButton(
            final IntegerField numberField,
            final NumberWriter numberWriter
    ) {
        this(
                "Add",
                numberField,
                numberWriter
        );
    }

    protected AddNumberButton(
            final String text,
            final IntegerField numberField,
            final NumberWriter numberWriter
    ) {
        super(text);
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
