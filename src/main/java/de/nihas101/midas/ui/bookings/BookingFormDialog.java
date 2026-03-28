package de.nihas101.midas.ui.bookings;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.entity.Source;
import de.nihas101.midas.bookings.service.BookingsWriter;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersReader;
import de.nihas101.midas.ui.common.CancelButton;
import de.nihas101.midas.ui.common.SaveButton;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static de.nihas101.midas.ui.common.DatePickerI18nProvider.datePickerI18n;

public class BookingFormDialog extends Dialog {

    private final BookingsWriter bookingsWriter;
    private final MessageSource messageSource;
    private final Locale locale;
    private final Consumer<Booking> onSave;

    private final Binder<Booking> binder = new Binder<>(Booking.class);
    private final Checkbox addAnotherCheckbox;

    public BookingFormDialog(
            final ShareholdersReader shareholdersReader,
            final BookingsWriter bookingsWriter,
            final MessageSource messageSource,
            final Locale locale,
            final Shareholder initialShareholder,
            final Consumer<Booking> onSave
    ) {
        this(shareholdersReader, bookingsWriter, messageSource, locale, initialShareholder, null, onSave);
    }

    public BookingFormDialog(
            final ShareholdersReader shareholdersReader,
            final BookingsWriter bookingsWriter,
            final MessageSource messageSource,
            final Locale locale,
            final Shareholder initialShareholder,
            final Booking bookingToEdit,
            final Consumer<Booking> onSave
    ) {
        this.bookingsWriter = bookingsWriter;
        this.messageSource = messageSource;
        this.locale = locale;
        this.onSave = onSave;

        final boolean isEditMode = bookingToEdit != null;
        final String titleKey = isEditMode ? "bookings.dialog.title.edit" : "bookings.dialog.title.add";
        setHeaderTitle(messageSource.getMessage(titleKey, null, locale));

        FormLayout formLayout = new FormLayout();

        ComboBox<Shareholder> shareholderPicker = new ComboBox<>(messageSource.getMessage("bookings.shareholder", null, locale));
        final List<Shareholder> shareholders = shareholdersReader.shareholders().toList();
        shareholderPicker.setItems(shareholders);
        shareholderPicker.setItemLabelGenerator(s -> s.getFirstName() + " " + s.getLastName() + " (" + s.getDisplayId() + ")");
        shareholderPicker.setRequired(true);
        binder.forField(shareholderPicker)
                .asRequired()
                .bind(
                        b -> shareholders.stream()
                                .filter(s -> s.getId().equals(b.getShareholderId()))
                                .findFirst()
                                .orElse(null),
                        (b, s) -> b.setShareholderId(s != null ? s.getId() : null)
                );

        // TODO: Extract into class, so we dont have to set the local everywhere
        DatePicker datePicker = new DatePicker(messageSource.getMessage("bookings.date", null, locale));
        datePicker.setLocale(locale);
        datePicker.setI18n(datePickerI18n(messageSource, locale));
        datePicker.setRequired(true);
        binder.forField(datePicker)
                .asRequired()
                .bind(Booking::getDate, Booking::setDate);

        ComboBox<BookingType> typePicker = new ComboBox<>(messageSource.getMessage("bookings.type", null, locale));
        typePicker.setItems(BookingType.creatableByUser());
        typePicker.setItemLabelGenerator(t -> messageSource.getMessage(t.getI18nKey(), null, locale) + " (" + t.getId() + ")");
        typePicker.setRequired(true);
        binder.forField(typePicker)
                .asRequired()
                .bind(Booking::getType, Booking::setType);

        TextField commentField = new TextField(messageSource.getMessage("bookings.comment", null, locale));
        binder.forField(commentField)
                .bind(Booking::getComment, Booking::setComment);

        // TODO: Extract into class, so we dont have to set the local everywhere
        BigDecimalField amountField = new BigDecimalField(messageSource.getMessage("bookings.amount", null, locale));
        amountField.setLocale(locale);
        amountField.setSuffixComponent(new Span("€")); // TODO: currency from config?
        binder.forField(amountField)
                .asRequired()
                .withConverter(
                        MoneyAmount::of,
                        m -> m != null ? m.toBigDecimalForInput() : null
                )
                .bind(Booking::getAmount, Booking::setAmount);

        formLayout.add(shareholderPicker, datePicker, typePicker, commentField, amountField);
        add(formLayout);

        addAnotherCheckbox = new Checkbox(messageSource.getMessage("bookings.add-another", null, locale));
        final HorizontalLayout checkBoxLayout = setupCheckBoxes(isEditMode);
        final HorizontalLayout buttonLayout = setupButtons(messageSource, locale);
        setupFooter(checkBoxLayout, buttonLayout);

        if (isEditMode) {
            binder.setBean(bookingToEdit);
        } else {
            Booking booking = new Booking();
            booking.setDate(LocalDate.now());
            booking.setSource(Source.USER);
            if (initialShareholder != null) {
                booking.setShareholderId(initialShareholder.getId());
                shareholderPicker.setValue(initialShareholder);
            }
            binder.setBean(booking);
        }
    }

    private HorizontalLayout setupButtons(final MessageSource messageSource, final Locale locale) {
        SaveButton saveButton = new SaveButton(messageSource.getMessage("bookings.dialog.save", null, locale), e -> save());
        CancelButton cancelButton = new CancelButton(messageSource.getMessage("bookings.dialog.cancel", null, locale), e -> close());
        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(saveButton, cancelButton);
        buttonLayout.setAlignItems(FlexComponent.Alignment.END);
        return buttonLayout;
    }

    private void setupFooter(final HorizontalLayout checkBoxLayout, final HorizontalLayout buttonLayout) {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setFlexGrow(1, addAnotherCheckbox);
        footer.add(checkBoxLayout, buttonLayout);
        getFooter().add(footer);
    }

    private HorizontalLayout setupCheckBoxes(final boolean isEditMode) {
        addAnotherCheckbox.setVisible(!isEditMode);
        final HorizontalLayout checkBoxLayout = new HorizontalLayout();
        checkBoxLayout.add(addAnotherCheckbox);
        checkBoxLayout.setWidthFull();
        return checkBoxLayout;
    }

    private void save() {
        if (!binder.validate().isOk()) {
            return;
        }

        try {
            Booking booking = binder.getBean();
            if (booking.getId() == null) {
                bookingsWriter.create(booking);
            } else {
                bookingsWriter.update(booking);
            }
            onSave.accept(booking);

            if (addAnotherCheckbox.isVisible() && addAnotherCheckbox.getValue()) {
                resetForm();
            } else {
                close();
            }
        } catch (Exception e) {
            Notification.show("Error saving booking: " + e.getMessage()); // TODO: i18n
        }
    }

    private void resetForm() {
        Booking current = binder.getBean();
        Booking next = new Booking();
        next.setShareholderId(current.getShareholderId());
        next.setDate(current.getDate());
        binder.setBean(next);
    }
}
