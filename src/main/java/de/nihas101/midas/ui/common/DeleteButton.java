package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

public class DeleteButton extends Button {

    public DeleteButton(
            final String label,
            final ComponentEventListener<ClickEvent<Button>> clickEvenListener
    ) {
        super(label);
        this.addThemeVariants(ButtonVariant.LUMO_ERROR);
        this.addClickListener(clickEvenListener);
    }
}
