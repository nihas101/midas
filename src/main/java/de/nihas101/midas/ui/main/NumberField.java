package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.textfield.IntegerField;

public class NumberField extends IntegerField {

    public NumberField() {
        IntegerField numberField = new IntegerField("Add a number");
        numberField.setPlaceholder("Enter number");
    }
}
