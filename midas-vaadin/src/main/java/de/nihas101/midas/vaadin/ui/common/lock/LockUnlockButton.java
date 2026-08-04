package de.nihas101.midas.vaadin.ui.common.lock;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.nihas101.midas.api.lock.LockWriter;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.vaadin.ui.common.ShareholderPicker;
import de.nihas101.midas.vaadin.ui.common.YearPicker;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.Locale;

// TODO: tests
public class LockUnlockButton extends Button {

    private final MessageSource messageSource;
    private final Locale locale;
    private final ShareholderLock shareholderLock;
    private final ShareholderPicker shareholderPicker;
    private final YearPicker yearPicker;
    private final LockWriter lockWriter;
    private final Runnable onUpdate;

    public LockUnlockButton(
            final MessageSource messageSource,
            final Locale locale,
            final ShareholderLock shareholderLock,
            final ShareholderPicker shareholderPicker,
            final YearPicker yearPicker,
            final LockWriter lockWriter,
            final Runnable onUpdate
    ) {
        this.messageSource = messageSource;
        this.locale = locale;
        this.shareholderLock = shareholderLock;
        this.shareholderPicker = shareholderPicker;
        this.yearPicker = yearPicker;
        this.lockWriter = lockWriter;
        this.onUpdate = onUpdate;

        this.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        this.addClickListener(e -> this.onLockUnlockClicked());
    }

    public void onLockUnlockClicked() {
        final Shareholder shareholder = shareholderPicker.getValue();
        final Integer yearValue = yearPicker.getValue();
        if (shareholder == null || yearValue == null) {
            return;
        }

        final Year year = Year.of(yearValue);
        lockUnlockDialog(
                shareholderLock.isLocked(shareholder, year),
                locale,
                year,
                shareholder
        ).open();
    }

    private ConfirmDialog lockUnlockDialog(
            final boolean isCurrentlyLocked,
            final Locale locale,
            final Year year,
            final Shareholder shareholder
    ) {
        final ConfirmDialog dialog;
        if (isCurrentlyLocked) {
            dialog = new UnlockDialog(
                    messageSource,
                    locale,
                    year,
                    shareholder,
                    e -> {
                        lockWriter.unlock(shareholder, year);
                        this.unlock();
                        onUpdate.run();
                    }
            );
        } else {
            dialog = new LockDialog(
                    messageSource,
                    locale,
                    year,
                    shareholder,
                    e -> {
                        lockWriter.lock(shareholder, year);
                        this.lock();
                        onUpdate.run();
                    }
            );
        }
        return dialog;
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
