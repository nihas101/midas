package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;

public class AddNumberLayout extends HorizontalLayout {

    public AddNumberLayout(final IntegerField numberField, Button addButton) {
        super(numberField, addButton);
        this.setAlignItems(FlexComponent.Alignment.BASELINE);
    }
}
