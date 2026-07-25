package de.nihas101.midas.ui.common.lock;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class LockUnlockButton extends Button {

    private final MessageSource messageSource;
    private final Locale locale;

    public LockUnlockButton(
            final MessageSource messageSource,
            final Locale locale,
            final ComponentEventListener<ClickEvent<Button>> clickEvenListener
    ) {
        this.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        this.addClickListener(clickEvenListener::onComponentEvent);
        this.messageSource = messageSource;
        this.locale = locale;
    }

    public void unlock() {
        setTooltipText(messageSource.getMessage("bookings.lock-year", null, locale));
        setIcon(VaadinIcon.UNLOCK.create());
        removeThemeVariants(ButtonVariant.LUMO_ERROR);
        addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    }

    public void lock() {
        setTooltipText(messageSource.getMessage("bookings.unlock-year", null, locale));
        setIcon(VaadinIcon.LOCK.create());
        removeThemeVariants(ButtonVariant.LUMO_CONTRAST);
        addThemeVariants(ButtonVariant.LUMO_ERROR);
    }
}
