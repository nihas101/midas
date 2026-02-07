package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import org.springframework.context.MessageSource;

public class AddNumberLayout extends HorizontalLayout {

    public AddNumberLayout(final IntegerField numberField, AddNumberButton addButton, MessageSource messageSource) {
        super(numberField, addButton);
        this.setAlignItems(FlexComponent.Alignment.BASELINE);
    }
}
