package de.nihas101.midas.ui.bookings;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.service.BookingsWriter;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersReader;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

public class BookingFormDialog extends Dialog {

    private final BookingsWriter bookingsWriter;
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
        this.bookingsWriter = bookingsWriter;
        this.onSave = onSave;

        setHeaderTitle(messageSource.getMessage("bookings.dialog.title.add", null, locale));

        FormLayout formLayout = new FormLayout();

        ComboBox<Shareholder> shareholderPicker = new ComboBox<>(messageSource.getMessage("bookings.shareholder", null, locale));
        final java.util.List<Shareholder> shareholders = shareholdersReader.shareholders().toList();
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

        // TODO: This shows mm/dd/yyyy, but this should be dd/mm/yyyy
        DatePicker datePicker = new DatePicker(messageSource.getMessage("bookings.date", null, locale));
        datePicker.setRequired(true);
        binder.forField(datePicker)
                .asRequired()
                .bind(Booking::getDate, Booking::setDate);

        ComboBox<BookingType> typePicker = new ComboBox<>(messageSource.getMessage("bookings.type", null, locale));
        typePicker.setItems(Arrays.asList(BookingType.values()));
        typePicker.setItemLabelGenerator(t -> messageSource.getMessage(t.getI18nKey(), null, locale) + " (" + t.getId() + ")");
        typePicker.setRequired(true);
        binder.forField(typePicker)
                .asRequired()
                .bind(Booking::getType, Booking::setType);

        TextField commentField = new TextField(messageSource.getMessage("bookings.comment", null, locale));
        binder.forField(commentField).bind(Booking::getComment, Booking::setComment);

        // TODO: Need to set the right comma here
        BigDecimalField amountField = new BigDecimalField(messageSource.getMessage("bookings.amount", null, locale));
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
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setFlexGrow(1, addAnotherCheckbox);

        Button saveButton = new Button(messageSource.getMessage("bookings.dialog.save", null, locale), e -> save());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button(messageSource.getMessage("bookings.dialog.cancel", null, locale), e -> close());

        footer.add(addAnotherCheckbox, saveButton, cancelButton);
        getFooter().add(footer);

        // Set initial values
        Booking booking = new Booking();
        booking.setDate(LocalDate.now());
        if (initialShareholder != null) {
            booking.setShareholderId(initialShareholder.getId());
            shareholderPicker.setValue(initialShareholder);
        }
        binder.setBean(booking);
    }

    private void save() {
        if (!binder.validate().isOk()) {
            return;
        }

        try {
            Booking booking = binder.getBean();
            bookingsWriter.create(booking);
            onSave.accept(booking);

            if (addAnotherCheckbox.getValue()) {
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
        next.setDate(current.getDate()); // Keep same date for convenience when adding batches
        binder.setBean(next);
    }
}
