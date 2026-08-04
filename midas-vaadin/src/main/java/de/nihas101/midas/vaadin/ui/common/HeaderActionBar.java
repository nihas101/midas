package de.nihas101.midas.vaadin.ui.common;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.nihas101.midas.api.lock.LockWriter;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.export.ExportFactory;
import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.vaadin.ui.common.lock.LockUnlockButton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.Locale;
import java.util.Set;

@Slf4j
public class HeaderActionBar extends HorizontalLayout {

    private final ShareholderPicker shareholderPicker; // TODO: Store the selected shareholder somewhere so that we can use that in the same filter when switching views
    private final YearPicker yearPicker; // TODO: Store the selected shareholder somewhere so that we can use that in the same filter when switching views
    private final LockUnlockButton lockUnlockButton;
    private final HorizontalLayout actionRow;
    private final PrintButton printButton;

    public HeaderActionBar(
            final MessageSource messageSource,
            final Locale locale,
            final ShareholderPicker shareholderPicker,
            final YearPicker yearPicker,
            final HorizontalLayout actionRow,
            final ShareholderLock shareholderLock,
            final LockWriter lockWriter,
            final Runnable onUpdate,
            final DownloadTrigger downloadTrigger,
            final ExportFactory exportFactory,
            final Set<ExportViewName> viewsToExport
    ) {
        this(
                shareholderPicker,
                new LockUnlockButton(
                        messageSource,
                        locale,
                        shareholderLock,
                        shareholderPicker,
                        yearPicker,
                        lockWriter,
                        onUpdate
                ),
                new PrintButton(
                        messageSource,
                        locale,
                        shareholderPicker,
                        yearPicker,
                        downloadTrigger,
                        exportFactory,
                        viewsToExport
                ),
                yearPicker,
                actionRow
        );
    }

    private HeaderActionBar(
            final ShareholderPicker shareholderPicker,
            final LockUnlockButton lockUnlockButton,
            final PrintButton printButton,
            final YearPicker yearPicker,
            final HorizontalLayout actionRow
    ) {
        this.shareholderPicker = shareholderPicker;
        this.yearPicker = yearPicker;
        this.lockUnlockButton = lockUnlockButton;
        this.printButton = printButton;

        this.lockUnlockButton.setVisible(false);
        this.printButton.setVisible(false);
        this.actionRow = actionRow;
        this.actionRow.setVisible(false);

        setWidthFull();
        setAlignItems(FlexComponent.Alignment.END);
        add(
                this.shareholderPicker,
                this.yearPicker,
                this.lockUnlockButton,
                this.printButton,
                this.actionRow
        );
        setFlexGrow(1, this.actionRow);
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
        printButton.setVisible(isVisible);
        actionRow.setVisible(isVisible);
    }

    public void lockLockUnlockButton() {
        lockUnlockButton.lock();
    }

    public void unlockLockUnlockButton() {
        lockUnlockButton.unlock();
    }
}
