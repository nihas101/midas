package de.nihas101.midas.ui.common.lock;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import de.nihas101.midas.core.shareholders.dto.Shareholder;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.Locale;

public class UnlockDialog extends ConfirmDialog {

    public UnlockDialog(
            final MessageSource messageSource,
            final Locale locale,
            final Year year,
            final Shareholder shareholder,
            final ComponentEventListener<ConfirmEvent> unlockConfirmEventListener
    ) {
        setHeader(messageSource.getMessage(
                        "bookings.unlock.confirmation.title", new Object[]{
                                year.toString()
                        },
                        locale
                )
        );
        setText(messageSource.getMessage(
                        "bookings.unlock.confirmation.message",
                        new Object[]{
                                year.toString(),
                                shareholder.getFirstName(),
                                shareholder.getLastName()
                        },
                        locale
                )
        );
        setConfirmText(messageSource.getMessage("bookings.unlock-year", null, locale));
        addConfirmListener(unlockConfirmEventListener);

        setCancelable(true);
        setCancelText(messageSource.getMessage("global.cancel", null, locale));
    }
}
