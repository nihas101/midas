package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.QueryParameters;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.lock.ShareholderLock;
import de.nihas101.midas.lock.service.LockWriter;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.common.lock.LockDialog;
import de.nihas101.midas.ui.common.lock.LockUnlockButton;
import de.nihas101.midas.ui.common.lock.UnlockDialog;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.Locale;

import static de.nihas101.midas.ui.common.MidasView.QUERY_PARAM_SHAREHOLDER;
import static de.nihas101.midas.ui.common.MidasView.QUERY_PARAM_YEAR;

public class HeaderActionBar<C extends Component> extends HorizontalLayout {

    private final ShareholderPicker shareholderPicker; // TODO: Store the selected shareholder somewhere so that we can use that in the same filter when switching views
    private final YearPicker yearPicker; // TODO: Store the selected shareholder somewhere so that we can use that in the same filter when switching views
    private final LockUnlockButton lockUnlockButton;
    private final MessageSource messageSource;
    private final Locale locale;
    private final Runnable refreshContent;
    private final LockWriter lockWriter;
    private final ShareholderLock shareholderLock;
    private final HorizontalLayout actionRow;

    public HeaderActionBar(
            final MessageSource messageSource,
            final Locale locale,
            final ShareholdersService shareholdersService,
            final MidasConfig midasConfig,
            final Class<C> viewClass,
            final HorizontalLayout actionRow,
            final Runnable refreshContent,
            final LockWriter lockWriter,
            final ShareholderLock shareholderLock
    ) {
        this.messageSource = messageSource;
        this.locale = locale;
        this.refreshContent = refreshContent;
        this.lockWriter = lockWriter;
        this.shareholderLock = shareholderLock;
        this.actionRow = actionRow;

        setWidthFull();
        setAlignItems(FlexComponent.Alignment.END);

        shareholderPicker = new ShareholderPicker(
                messageSource.getMessage("bookings.shareholder", null, locale),
                messageSource.getMessage("shareholder-picker.placeholder", null, locale),
                shareholdersService,
                e -> {
                    final Shareholder shareholder = e.getValue();

                    QueryParameters queryParameters = UI.getCurrent().getActiveViewLocation().getQueryParameters();
                    if (shareholder != null) {
                        queryParameters = queryParameters.merging(QUERY_PARAM_SHAREHOLDER, String.valueOf(shareholder.getId()));
                    } else {
                        queryParameters = queryParameters.excluding(QUERY_PARAM_SHAREHOLDER);
                    }
                    UI.getCurrent().navigate(viewClass, queryParameters);
                    refreshContent.run();
                }
        );
        yearPicker = new YearPicker(
                messageSource.getMessage("bookings.year", null, locale),
                e -> {
                    final Integer year = e.getValue();

                    QueryParameters queryParameters = UI.getCurrent().getActiveViewLocation().getQueryParameters();
                    if (year != null) {
                        queryParameters = queryParameters.merging(QUERY_PARAM_YEAR, String.valueOf(String.valueOf(year)));
                    } else {
                        queryParameters = queryParameters.excluding(QUERY_PARAM_YEAR);
                    }
                    UI.getCurrent().navigate(viewClass, queryParameters);
                    refreshContent.run();
                },
                midasConfig
        );

        lockUnlockButton = new LockUnlockButton(
                messageSource,
                locale,
                e -> onLockUnlockClicked()
        );
        lockUnlockButton.setVisible(false);
        actionRow.setVisible(false);

        add(
                shareholderPicker,
                yearPicker,
                lockUnlockButton,
                actionRow
        );
        setFlexGrow(1, actionRow);
    }

    private void onLockUnlockClicked() {
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
                        lockUnlockButton.unlock();
                        refreshContent.run();
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
                        lockUnlockButton.lock();
                        refreshContent.run();
                    }
            );
        }
        return dialog;
    }

    public Shareholder getSelectedShareholder() {
        return shareholderPicker.getValue();
    }

    public void setSelectedShareholder(final Shareholder shareholder) {
        this.shareholderPicker.setValue(shareholder);
    }

    public Year getSelectedYear() {
        return yearPicker != null ? Year.of(yearPicker.getValue()) : null;
    }

    public void setSelectedYear(final Integer year) {
        yearPicker.setValue(year);
    }

    public void setActionButtonsVisible(final boolean isVisible) {
        lockUnlockButton.setVisible(isVisible);
        actionRow.setVisible(isVisible);
    }

    public void lockLockUnlockButton() {
        lockUnlockButton.lock();
    }

    public void unlockLockUnlockButton() {
        lockUnlockButton.unlock();
    }
}
