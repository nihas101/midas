package de.nihas101.midas.vaadin.ui.bookings;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingFactory;
import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.api.bookings.BookingsWriter;
import de.nihas101.midas.api.commenttemplate.CommentTemplatesReader;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.config.UIConfig;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.vaadin.ui.common.CancelButton;
import de.nihas101.midas.vaadin.ui.common.SaveButton;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.Year;
import java.util.Locale;
import java.util.function.Consumer;

// TODO: Add tests
public class CreateBookingFormDialog extends BookingFormDialog {

    private final BookingsReader bookingsReader;
    private final BookingsWriter bookingsWriter;
    private final Consumer<Booking> onSave;
    private final Checkbox addAnotherCheckbox;
    private final BookingFactory bookingFactory;

    public CreateBookingFormDialog(
            final ShareholdersReader shareholdersReader,
            final BookingsReader bookingsReader,
            final BookingsWriter bookingsWriter,
            final CommentTemplatesReader commentTemplatesReader,
            final MessageSource messageSource,
            final Locale locale,
            final Shareholder initialShareholder,
            final ShareholderLock shareholderLock,
            final Consumer<Booking> onSave,
            final UIConfig uiConfig,
            final BookingFactory bookingFactory
    ) {
        super(
                messageSource,
                "bookings.dialog.title.add",
                locale,
                shareholdersReader,
                shareholderLock,
                commentTemplatesReader,
                uiConfig
        );
        this.bookingsReader = bookingsReader;
        this.bookingsWriter = bookingsWriter;
        this.onSave = onSave;
        this.bookingFactory = bookingFactory;

        this.addAnotherCheckbox = new Checkbox(messageSource.getMessage("bookings.add-another", null, locale));
        addAnotherCheckbox.setValue(uiConfig.isDefaultAddAnotherCheckboxState());
        final HorizontalLayout checkBoxLayout = setupCheckBoxes();
        final HorizontalLayout buttonLayout = setupButtons(messageSource, locale);
        setupFooter(checkBoxLayout, buttonLayout);

        final Booking booking = bookingFactory.create(
                LocalDate.now(),
                Source.USER
        );
        if (initialShareholder != null) {
            booking.setShareholderId(initialShareholder.getId());
            shareholderPicker.setValue(initialShareholder);
        }
        binder.setBean(booking);
        updateCommentSuggestions(commentPicker, null);
    }

    private HorizontalLayout setupButtons(final MessageSource messageSource, final Locale locale) {
        final SaveButton saveButton = new SaveButton(messageSource.getMessage("bookings.dialog.save", null, locale), e -> save());
        CancelButton cancelButton = new CancelButton(messageSource.getMessage("bookings.dialog.cancel", null, locale), e -> close());
        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(saveButton, cancelButton);
        buttonLayout.setAlignItems(FlexComponent.Alignment.END);
        return buttonLayout;
    }

    private void setupFooter(final HorizontalLayout checkBoxLayout, final HorizontalLayout buttonLayout) {
        final HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setFlexGrow(1, addAnotherCheckbox);
        footer.add(checkBoxLayout, buttonLayout);
        getFooter().add(footer);
    }

    private HorizontalLayout setupCheckBoxes() {
        final HorizontalLayout checkBoxLayout = new HorizontalLayout();
        checkBoxLayout.add(addAnotherCheckbox);
        checkBoxLayout.setWidthFull();
        return checkBoxLayout;
    }

    private void save() {
        final BinderValidationStatus<Booking> validationStatus = binder.validate();
        if (!validationStatus.isOk()) {
            validationStatus.getValidationErrors()
                    .stream()
                    .map(ValidationResult::getErrorMessage)
                    .filter(StringUtils::isNotBlank)
                    .findFirst()
                    .ifPresent(Notification::show);
            return;
        }

        final Booking booking = binder.getBean();
        final Shareholder selectedShareholder = shareholderPicker.getValue();
        if (shareholderLock != null && selectedShareholder != null && booking != null && booking.getDate() != null) {
            if (shareholderLock.isLocked(selectedShareholder, Year.of(booking.getDate().getYear()))) {
                Notification.show(messageSource.getMessage("bookings.save.error", new Object[]{"Year is locked"}, locale));
                return;
            }
        }

        if (bookingsReader.exists(booking)) {
            final ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader(messageSource.getMessage("bookings.dialog.doublebooking.warning.title", null, locale));
            confirmDialog.setText(messageSource.getMessage("bookings.dialog.doublebooking.warning.message", null, locale));
            confirmDialog.setCancelable(true);
            confirmDialog.setCancelText(messageSource.getMessage("global.cancel", null, locale));
            confirmDialog.setConfirmText(messageSource.getMessage("bookings.dialog.doublebooking.warning.confirm", null, locale));
            confirmDialog.addConfirmListener(e -> persistAndClose(booking));
            confirmDialog.open();
        } else {
            persistAndClose(booking);
        }
    }

    private void persistAndClose(final Booking booking) {
        try {
            if (booking.getId() == null) {
                bookingsWriter.create(booking);
            } else {
                bookingsWriter.update(booking);
            }
            onSave.accept(booking);

            if (addAnotherCheckbox.getValue()) {
                resetForm();
            } else {
                close();
            }
        } catch (Exception e) {
            Notification.show(messageSource.getMessage("bookings.save.error", new Object[]{e.getMessage()}, locale));
        }
    }

    private void resetForm() {
        final Booking current = binder.getBean();
        final Booking next = bookingFactory.create(
                current.getShareholderId(),
                current.getDate()
        );
        binder.setBean(next);
    }
}
