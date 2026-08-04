package de.nihas101.midas.vaadin.ui.common;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;

public class EditButton extends Button {

    public EditButton(
            final String toolTip,
            final ComponentEventListener<ClickEvent<Button>> clickEvenListener
    ) {
        this(
                "",
                toolTip,
                clickEvenListener
        );
    }

    public EditButton(
            final String label,
            final String toolTip,
            final ComponentEventListener<ClickEvent<Button>> clickEvenListener
    ) {
        super(label, VaadinIcon.EDIT.create());
        this.setTooltipText(toolTip);
        this.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        this.addClickListener(clickEvenListener);
    }
}
