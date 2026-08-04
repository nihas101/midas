package de.nihas101.midas.vaadin.ui.common;

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
        this.setDisableOnClick(true);
        this.addClickListener(e -> {
            try {
                clickEvenListener.onComponentEvent(e);
            } finally {
                e.getSource().setEnabled(true);
            }
        });
    }
}
