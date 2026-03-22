package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

public class AddButton extends Button {

    public AddButton(
            final String label,
            final String toolTip,
            final ComponentEventListener<ClickEvent<Button>> clickEvenListener
    ) {
        super(label);
        this.setTooltipText(toolTip);
        this.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        this.addClickListener(clickEvenListener);
    }
}
