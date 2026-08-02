package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;

public class DeleteButton extends Button {

    public DeleteButton(
            final String toolTip,
            final ComponentEventListener<ClickEvent<Button>> clickEvenListener
    ) {
        this("", toolTip, clickEvenListener);
    }

    public DeleteButton(
            final String label,
            final String toolTip,
            final ComponentEventListener<ClickEvent<Button>> clickEvenListener
    ) {
        super(label, VaadinIcon.TRASH.create());
        this.setTooltipText(toolTip);
        this.addThemeVariants(ButtonVariant.LUMO_ERROR);
        this.addClickListener(clickEvenListener);
    }
}
