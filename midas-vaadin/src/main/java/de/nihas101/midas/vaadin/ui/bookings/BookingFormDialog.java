package de.nihas101.midas.vaadin.ui.bookings;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
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
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.api.bookings.BookingsWriter;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.bookings.dto.DefaultBooking;
import de.nihas101.midas.core.config.UIConfig;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.vaadin.ui.common.CancelButton;
import de.nihas101.midas.vaadin.ui.common.SaveButton;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static de.nihas101.midas.vaadin.ui.common.DatePickerI18nProvider.datePickerI18n;

// TODO: Add tests
// TODO: Separate into edit and create variants
public class BookingFormDialog extends Dialog {

    private final BookingsReader bookingsReader;
    private final BookingsWriter bookingsWriter;
    private final ShareholderLock shareholderLock;
    private final Consumer<Booking> onSave;

    private final Binder<Booking> binder = new Binder<>(Booking.class);
    private final Checkbox addAnotherCheckbox;
    private final MessageSource messageSource;
    private final Locale locale;
    private final ComboBox<Shareholder> shareholderPicker;
    private final DatePicker datePicker;

    public BookingFormDialog(
            final ShareholdersReader shareholdersReader,
            final BookingsReader bookingsReader,
            final BookingsWriter bookingsWriter,
            final MessageSource messageSource,
            final Locale locale,
            final Shareholder initialShareholder,
            final Consumer<Booking> onSave,
            final UIConfig uiConfig
    ) {
        this(
                shareholdersReader,
                bookingsReader,
                bookingsWriter,
                messageSource,
                locale,
                initialShareholder,
                null,
                null,
                onSave,
                uiConfig
        );
    }

    public BookingFormDialog(
            final ShareholdersReader shareholdersReader,
            final BookingsReader bookingsReader,
            final BookingsWriter bookingsWriter,
            final MessageSource messageSource,
            final Locale locale,
            final Shareholder initialShareholder,
            final ShareholderLock shareholderLock,
            final Consumer<Booking> onSave,
            final UIConfig uiConfig
    ) {
        this(
                shareholdersReader,
                bookingsReader,
                bookingsWriter,
                messageSource,
                locale,
                initialShareholder,
                null,
                shareholderLock,
                onSave,
                uiConfig
        );
    }

    public BookingFormDialog(
            final ShareholdersReader shareholdersReader,
            final BookingsReader bookingsReader,
            final BookingsWriter bookingsWriter,
            final MessageSource messageSource,
            final Locale locale,
            final Shareholder initialShareholder,
            final Booking bookingToEdit,
            final Consumer<Booking> onSave,
            final UIConfig uiConfig
    ) {
        this(
                shareholdersReader,
                bookingsReader,
                bookingsWriter,
                messageSource,
                locale,
                initialShareholder,
                bookingToEdit,
                null,
                onSave,
                uiConfig
        );
    }

    public BookingFormDialog(
            final ShareholdersReader shareholdersReader,
            final BookingsReader bookingsReader,
            final BookingsWriter bookingsWriter,
            final MessageSource messageSource,
            final Locale locale,
            final Shareholder initialShareholder,
            final Booking bookingToEdit,
            final ShareholderLock shareholderLock,
            final Consumer<Booking> onSave,
            final UIConfig uiConfig
    ) {
        this.bookingsReader = bookingsReader;
        this.bookingsWriter = bookingsWriter;
        this.shareholderLock = shareholderLock;
        this.messageSource = messageSource;
        this.locale = locale;
        this.onSave = onSave;

        final boolean isEditMode = bookingToEdit != null;
        final String titleKey = isEditMode ? "bookings.dialog.title.edit" : "bookings.dialog.title.add";
        setHeaderTitle(messageSource.getMessage(titleKey, null, locale));

        final FormLayout formLayout = new FormLayout();

        shareholderPicker = new ComboBox<>(messageSource.getMessage("bookings.shareholder", null, locale));
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

        datePicker = datePicker(messageSource, locale, shareholderLock);
        final ComboBox<BookingType> typePicker = typePicker(messageSource, locale);
        final TextField commentField = commentField(messageSource, locale);
        final BigDecimalField amountField = amountField(messageSource, locale);

        formLayout.add(shareholderPicker, datePicker, typePicker, commentField, amountField);
        add(formLayout);

        addAnotherCheckbox = new Checkbox(messageSource.getMessage("bookings.add-another", null, locale));
        addAnotherCheckbox.setValue(uiConfig.isDefaultAddAnotherCheckboxState());
        final HorizontalLayout checkBoxLayout = setupCheckBoxes(isEditMode);
        final HorizontalLayout buttonLayout = setupButtons(messageSource, locale);
        setupFooter(checkBoxLayout, buttonLayout);

        if (isEditMode) {
            binder.setBean(bookingToEdit);
        } else {
            final Booking booking = new DefaultBooking();
            booking.setDate(LocalDate.now());
            booking.setSource(Source.USER);
            if (initialShareholder != null) {
                booking.setShareholderId(initialShareholder.getId());
                shareholderPicker.setValue(initialShareholder);
            }
            binder.setBean(booking);
        }
    }

    private BigDecimalField amountField(final MessageSource messageSource, final Locale locale) {
        // TODO: Extract into class, so we dont have to set the local everywhere
        BigDecimalField amountField = new BigDecimalField(messageSource.getMessage("bookings.amount", null, locale));
        amountField.setLocale(locale);
        amountField.setSuffixComponent(new Span("€")); // TODO: currency from config?
        binder.forField(amountField)
                .asRequired()
                .withValidator((Validator<BigDecimal>) (value, context) -> value.longValue() != 0L
                                ? ValidationResult.ok()
                                : ValidationResult.error(
                                messageSource.getMessage(
                                        "bookings.amount.error",
                                        null,
                                        locale
                                )
                        )
                ).withConverter(
                        MoneyAmount::of,
                        m -> m != null ? m.toBigDecimalForInput() : null
                )
                .bind(Booking::getAmount, Booking::setAmount);
        return amountField;
    }

    private TextField commentField(final MessageSource messageSource, final Locale locale) {
        final TextField commentField = new TextField(messageSource.getMessage("bookings.comment", null, locale));
        binder.forField(commentField)
                .bind(Booking::getComment, Booking::setComment);
        return commentField;
    }

    private ComboBox<BookingType> typePicker(final MessageSource messageSource, final Locale locale) {
        final ComboBox<BookingType> typePicker = new ComboBox<>(messageSource.getMessage("bookings.type", null, locale));
        typePicker.setItems(BookingType.creatableByUser());
        typePicker.setItemLabelGenerator(t -> messageSource.getMessage(t.getI18nKey(), null, locale) + " (" + t.getId() + ")");
        typePicker.setRequired(true);
        binder.forField(typePicker)
                .asRequired()
                .bind(Booking::getType, Booking::setType);
        return typePicker;
    }

    private DatePicker datePicker(final MessageSource messageSource, final Locale locale, final ShareholderLock shareholderLock) {
        // TODO: Extract into class, so we dont have to set the local everywhere
        final DatePicker datePicker = new DatePicker(messageSource.getMessage("bookings.date", null, locale));
        datePicker.setLocale(locale);
        datePicker.setI18n(datePickerI18n(messageSource, locale));
        datePicker.setRequired(true);
        binder.forField(datePicker)
                .asRequired()
                .withValidator((Validator<LocalDate>) (value, context) -> {
                    final Shareholder shareholder = shareholderPicker.getValue();
                    final Year year = Year.of(this.datePicker.getValue().getYear());
                    final boolean isLocked = shareholderLock.isLocked(shareholder, year);
                    return isLocked ? ValidationResult.error(
                            messageSource.getMessage(
                                    "bookings.lock.error.year-locked",
                                    new Object[]{
                                            year.toString(),
                                            shareholder.getFirstName(),
                                            shareholder.getLastName()
                                    },
                                    locale
                            ))
                            : ValidationResult.ok();
                })
                .bind(Booking::getDate, Booking::setDate);
        return datePicker;
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

    private HorizontalLayout setupCheckBoxes(final boolean isEditMode) {
        addAnotherCheckbox.setVisible(!isEditMode);
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

            if (addAnotherCheckbox.isVisible() && addAnotherCheckbox.getValue()) {
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
        final Booking next = new DefaultBooking();
        next.setShareholderId(current.getShareholderId());
        next.setDate(current.getDate());
        binder.setBean(next);
    }
}
