package de.nihas101.midas.ui.shareholders;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersReader;
import de.nihas101.midas.shareholders.service.ShareholdersWriter;
import de.nihas101.midas.ui.main.Dependant;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

public class ShareholdersTable extends Grid<Shareholder> implements Dependant {

    private final ShareholdersReader shareholdersReader;
    private final ShareholdersWriter shareholdersWriter;

    public ShareholdersTable(
            final ShareholdersReader shareholdersReader,
            final ShareholdersWriter shareholdersWriter,
            final MessageSource messageSource,
            final Locale locale
    ) {
        super(Shareholder.class);
        this.shareholdersReader = shareholdersReader;
        this.shareholdersWriter = shareholdersWriter;
        this.setColumns(); // Clear auto-generated columns to manually add them with editors

        final Binder<Shareholder> binder = new Binder<>(Shareholder.class);
        final Editor<Shareholder> editor = this.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        setupDisplayIdColumn(messageSource, locale);
        setupFirstNameField(messageSource, locale, binder);
        setupLastNameField(messageSource, locale, binder);
        setupExternalIdField(messageSource, locale, binder);

        setupActionsColumn(messageSource, locale, editor);
        setupEditorActions(messageSource, locale, editor);

        addSaveListener(shareholdersWriter, editor);

        this.setAllRowsVisible(true);
        this.update();
    }

    private void addSaveListener(final ShareholdersWriter shareholdersWriter, final Editor<Shareholder> editor) {
        editor.addSaveListener(e -> {
            final Shareholder item = e.getItem();
            if (item.getId() == null) {
                shareholdersWriter.create(item);
            } else {
                shareholdersWriter.update(item);
            }
            this.update(); // Refetch everything and re-add dummy row
        });
    }

    private void setupEditorActions(
            final MessageSource messageSource,
            final Locale locale,
            final Editor<Shareholder> editor
    ) {
        final HorizontalLayout actions = new HorizontalLayout();
        final Button saveButton = new Button(messageSource.getMessage("global.save", null, locale), e -> editor.save());
        final Button cancelButton = new Button(messageSource.getMessage("global.cancel", null, locale), e -> editor.cancel());
        actions.add(saveButton, cancelButton);

        this.getColumns().getLast().setEditorComponent(actions);
    }

    private void setupActionsColumn(
            final MessageSource messageSource,
            final Locale locale,
            final Editor<Shareholder> editor
    ) {
        this.addComponentColumn(shareholder -> {
                    final boolean isDummy = shareholder.getId() == null;

                    final HorizontalLayout actions = new HorizontalLayout();

                    final String editButtonText = isDummy
                            ? messageSource.getMessage("shareholder.add.button", null, locale)
                            : messageSource.getMessage("global.edit", null, locale);

                    final Button editButton = new Button(editButtonText);
                    editButton.addClickListener(e -> {
                        if (editor.isOpen()) {
                            editor.cancel();
                        }
                        editor.editItem(shareholder);
                    });

                    actions.add(editButton);

                    if (!isDummy) {
                        final Button deleteButton = createDeleteShareholderButton(messageSource, locale, shareholder);
                        actions.add(deleteButton);
                    }

                    return actions;
                }).setHeader(messageSource.getMessage("shareholders.table.actions", null, locale))
                .setAutoWidth(true);
    }

    private Button createDeleteShareholderButton(
            final MessageSource messageSource,
            final Locale locale,
            final Shareholder shareholder
    ) {
        final Button deleteButton = new Button(messageSource.getMessage("global.delete", null, locale));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            final ConfirmDialog dialog = createDeleteShareholderDialog(messageSource, locale, shareholder);
            dialog.open();
        });
        return deleteButton;
    }

    private ConfirmDialog createDeleteShareholderDialog(final MessageSource messageSource, final Locale locale, final Shareholder shareholder) {
        final ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(messageSource.getMessage("shareholders.table.delete.confirmation.title", null, locale));

        final String[] args = new String[]{
                shareholder.getFirstName(),
                shareholder.getLastName(),
                "ID: " + shareholder.getDisplayId()
        };
        dialog.setText(messageSource.getMessage("shareholders.table.delete.confirmation.message", args, locale));

        dialog.setCancelable(true);
        dialog.setCancelText(messageSource.getMessage("global.cancel", null, locale));
        dialog.setConfirmText(messageSource.getMessage("global.delete", null, locale));
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> {
            shareholdersWriter.delete(shareholder);
            this.update();
        });
        return dialog;
    }

    private void setupExternalIdField(
            final MessageSource messageSource,
            final Locale locale,
            final Binder<Shareholder> binder
    ) {
        final IntegerField externalIdField = new IntegerField();
        externalIdField.setWidthFull();

        binder.forField(externalIdField)
                .bind(Shareholder::getExternalId, Shareholder::setExternalId);

        this.addColumn(Shareholder::getExternalId)
                .setHeader(messageSource.getMessage("shareholders.table.external_id", null, locale))
                .setKey("externalId")
                .setAutoWidth(true);

        this.getColumnByKey("externalId").setEditorComponent(externalIdField);
    }

    private void setupLastNameField(
            final MessageSource messageSource,
            final Locale locale,
            final Binder<Shareholder> binder
    ) {
        final TextField lastNameField = new TextField();
        lastNameField.setWidthFull();
        binder.forField(lastNameField)
                .asRequired(messageSource.getMessage("shareholder.last-name.label", null, locale) + " is required") // TODO: i18n
                .bind(Shareholder::getLastName, Shareholder::setLastName);

        this.addColumn(Shareholder::getLastName)
                .setHeader(messageSource.getMessage("shareholders.table.last-name", null, locale))
                .setKey("lastName")
                .setAutoWidth(true);

        this.getColumnByKey("lastName").setEditorComponent(lastNameField);
    }

    private void setupFirstNameField(
            final MessageSource messageSource,
            final Locale locale,
            final Binder<Shareholder> binder
    ) {
        final TextField firstNameField = new TextField();
        firstNameField.setWidthFull();

        binder.forField(firstNameField)
                .asRequired(messageSource.getMessage("shareholder.first-name.label", null, locale) + " is required") // TODO: i18n
                .bind(Shareholder::getFirstName, Shareholder::setFirstName);

        this.addColumn(Shareholder::getFirstName)
                .setHeader(messageSource.getMessage("shareholders.table.first-name", null, locale))
                .setKey("firstName")
                .setAutoWidth(true);

        this.getColumnByKey("firstName").setEditorComponent(firstNameField);
    }

    private void setupDisplayIdColumn(final MessageSource messageSource, final Locale locale) {
        this.addColumn(shareholder -> shareholder.getId() == null ? "" : String.valueOf(shareholder.getDisplayId()))
                .setHeader(messageSource.getMessage("shareholders.table.display_id", null, locale))
                .setKey("displayId")
                .setAutoWidth(true);
    }

    @Override
    public void update() {
        final List<Shareholder> shareholders = shareholdersReader.shareholders().toList();

        // Add permanent empty row for new shareholder
        Shareholder dummy = new Shareholder();
        shareholders.add(dummy);

        this.setItems(shareholders);
    }
}
