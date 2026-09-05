package de.nihas101.midas.vaadin.ui.bookings;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.commenttemplate.CommentTemplatesReader;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.core.config.UIConfig;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.vaadin.ui.common.MoneyAmountField;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Locale;

import static de.nihas101.midas.vaadin.ui.common.DatePickerI18nProvider.datePickerI18n;
import static java.util.Collections.emptyList;

public class BookingFormDialog extends Dialog {

    protected final Binder<Booking> binder = new Binder<>(Booking.class);
    protected final ComboBox<Shareholder> shareholderPicker;
    protected final DatePicker datePicker;
    protected final CommentTemplatesReader commentTemplatesReader;
    protected final UIConfig config;
    protected final FormLayout formLayout;
    protected final ComboBox<String> commentPicker;
    protected final ComboBox<BookingType> typePicker;
    protected final ShareholderLock shareholderLock;
    protected final MessageSource messageSource;
    protected final Locale locale;

    public BookingFormDialog(
            MessageSource messageSource,
            String titleKey,
            Locale locale,
            ShareholdersReader shareholdersReader,
            ShareholderLock shareholderLock,
            CommentTemplatesReader commentTemplatesReader,
            UIConfig config
    ) {
        this.messageSource = messageSource;
        this.locale = locale;
        this.config = config;
        this.commentTemplatesReader = commentTemplatesReader;
        this.shareholderLock = shareholderLock;

        setHeaderTitle(messageSource.getMessage(titleKey, null, locale));

        formLayout = new FormLayout();

        shareholderPicker = shareholderPicker(shareholdersReader, messageSource, locale);

        datePicker = datePicker(messageSource, locale, shareholderLock);
        typePicker = typePicker(messageSource, locale);
        commentPicker = commentPicker(messageSource, locale);
        typePicker.addValueChangeListener(e -> updateCommentSuggestions(commentPicker, e.getValue()));

        final MoneyAmountField<Booking> moneyAmountField = new MoneyAmountField<>(
                messageSource,
                this.binder,
                messageSource.getMessage("bookings.amount", null, locale),
                locale,
                this.config,
                Booking::getAmount,
                Booking::setAmount
        );

        formLayout.add(shareholderPicker, datePicker, typePicker, commentPicker, moneyAmountField);
        add(formLayout);
    }

    private ComboBox<Shareholder> shareholderPicker(
            final ShareholdersReader shareholdersReader,
            final MessageSource messageSource,
            final Locale locale
    ) {
        final ComboBox<Shareholder> shareholderPicker;
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
        return shareholderPicker;
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
                    final boolean isLocked = shareholderLock != null && shareholderLock.isLocked(shareholder, year);
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

    private ComboBox<String> commentPicker(
            final MessageSource messageSource,
            final Locale locale
    ) {
        final ComboBox<String> commentPicker = new ComboBox<>(messageSource.getMessage("bookings.comment", null, locale));
        commentPicker.setAllowCustomValue(true);
        commentPicker.addCustomValueSetListener(e -> commentPicker.setValue(e.getDetail()));
        binder.forField(commentPicker)
                .bind(Booking::getComment, Booking::setComment);
        return commentPicker;
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

    protected void updateCommentSuggestions(final ComboBox<String> commentPicker, final BookingType bookingType) {
        if (commentTemplatesReader != null) {
            commentPicker.setItems(commentTemplatesReader.getSuggestions(bookingType));
        } else {
            commentPicker.setItems(emptyList());
        }
    }
}
