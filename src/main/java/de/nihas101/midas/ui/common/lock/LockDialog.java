package de.nihas101.midas.ui.common.lock;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import de.nihas101.midas.shareholders.dto.Shareholder;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.Locale;

public class LockDialog extends ConfirmDialog {

    public LockDialog(
            final MessageSource messageSource,
            final Locale locale,
            final Year year,
            final Shareholder shareholder,
            final ComponentEventListener<ConfirmEvent> lockConfirmEventListener
    ) {
        setHeader(messageSource.getMessage(
                        "bookings.lock.confirmation.title",
                        new Object[]{
                                year.toString()
                        }, locale
                )
        );
        setText(messageSource.getMessage(
                        "bookings.lock.confirmation.message",
                        new Object[]{
                                year.toString(),
                                shareholder.getFirstName(),
                                shareholder.getLastName()},
                        locale
                )
        );
        setConfirmText(messageSource.getMessage("bookings.lock-year", null, locale));
        addConfirmListener(lockConfirmEventListener);

        setCancelable(true);
        setCancelText(messageSource.getMessage("global.cancel", null, locale));
    }
}
