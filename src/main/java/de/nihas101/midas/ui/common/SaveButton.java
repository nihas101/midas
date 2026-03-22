package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

public class SaveButton extends Button {

    public SaveButton(
            final String label,
            final ComponentEventListener<ClickEvent<Button>> clickEvenListener
    ) {
        super(label);
        this.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.addClickListener(clickEvenListener);
    }
}
