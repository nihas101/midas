package de.nihas101.midas.vaadin.ui.commenttemplate;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import de.nihas101.midas.api.commenttemplate.CommentTemplate;
import de.nihas101.midas.api.commenttemplate.CommentTemplatesWriter;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.core.commenttemplate.dto.DefaultCommentTemplate;
import de.nihas101.midas.vaadin.ui.common.CancelButton;
import de.nihas101.midas.vaadin.ui.common.SaveButton;
import org.springframework.context.MessageSource;

import java.util.HashSet;
import java.util.Locale;
import java.util.function.Consumer;

public class CommentTemplateFormDialog extends Dialog {

    private final CommentTemplatesWriter commentTemplatesWriter;
    private final MessageSource messageSource;
    private final Locale locale;
    private final Consumer<CommentTemplate> onSave;

    private final Binder<DefaultCommentTemplate> binder = new Binder<>(DefaultCommentTemplate.class);
    private final TextField textFilter;
    private final MultiSelectComboBox<BookingType> bookingTypesPicker;

    public CommentTemplateFormDialog(
            final CommentTemplatesWriter commentTemplatesWriter,
            final MessageSource messageSource,
            final Locale locale,
            final CommentTemplate templateToEdit,
            final Consumer<CommentTemplate> onSave
    ) {
        this.commentTemplatesWriter = commentTemplatesWriter;
        this.messageSource = messageSource;
        this.locale = locale;
        this.onSave = onSave;

        final boolean isEditMode = templateToEdit != null;
        final String titleKey = isEditMode ? "comment-templates.dialog.title.edit" : "comment-templates.dialog.title.add";
        setHeaderTitle(messageSource.getMessage(titleKey, null, locale));

        final FormLayout formLayout = new FormLayout();

        textFilter = new TextField(messageSource.getMessage("comment-templates.text", null, locale));
        textFilter.setRequired(true);
        binder.forField(textFilter)
                .asRequired()
                .bind(DefaultCommentTemplate::getText, DefaultCommentTemplate::setText);

        bookingTypesPicker = new MultiSelectComboBox<>(messageSource.getMessage("comment-templates.types", null, locale));
        bookingTypesPicker.setItems(BookingType.creatableByUser());
        bookingTypesPicker.setItemLabelGenerator(t -> messageSource.getMessage(t.getI18nKey(), null, locale));
        bookingTypesPicker.setPlaceholder(messageSource.getMessage("comment-templates.types.placeholder", null, locale));

        binder.forField(bookingTypesPicker)
                .bind(DefaultCommentTemplate::getBookingTypes, (t, types) -> {
                    t.getBookingTypes().clear();
                    if (types != null) {
                        t.getBookingTypes().addAll(types);
                    }
                });

        formLayout.add(textFilter, bookingTypesPicker);
        add(formLayout);

        setupFooter();

        if (isEditMode) {
            final DefaultCommentTemplate templateBean = DefaultCommentTemplate.builder()
                    .id(templateToEdit.getId())
                    .text(templateToEdit.getText())
                    .bookingTypes(templateToEdit.getBookingTypes() != null ? new HashSet<>(templateToEdit.getBookingTypes()) : new HashSet<>())
                    .build();
            binder.setBean(templateBean);
        } else {
            binder.setBean(new DefaultCommentTemplate());
        }
    }

    private void setupFooter() {
        final SaveButton saveButton = new SaveButton(messageSource.getMessage("global.save", null, locale), e -> save());
        final CancelButton cancelButton = new CancelButton(messageSource.getMessage("global.cancel", null, locale), e -> close());

        final HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setAlignItems(FlexComponent.Alignment.END);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        getFooter().add(buttonLayout);
    }

    private void save() {
        if (!binder.validate().isOk()) {
            return;
        }

        try {
            final DefaultCommentTemplate templateBean = binder.getBean();
            final CommentTemplate saved = commentTemplatesWriter.save(templateBean);
            if (onSave != null) {
                onSave.accept(saved);
            }
            close();
        } catch (Exception e) {
            Notification.show(e.getMessage());
        }
    }
}
