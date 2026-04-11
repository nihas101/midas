package de.nihas101.midas.ui.shareholders;

import com.vaadin.flow.component.button.Button;
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
import de.nihas101.midas.ui.common.AddButton;
import de.nihas101.midas.ui.common.CancelButton;
import de.nihas101.midas.ui.common.DeleteButton;
import de.nihas101.midas.ui.common.EditButton;
import de.nihas101.midas.ui.common.SaveButton;
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

        setupDisplayIdColumn(messageSource, locale, binder);
        setupFirstNameField(messageSource, locale, binder);
        setupLastNameField(messageSource, locale, binder);

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
        final SaveButton saveButton = new SaveButton(messageSource.getMessage("global.save", null, locale), e -> editor.save());
        final CancelButton cancelButton = new CancelButton(messageSource.getMessage("global.cancel", null, locale), e -> editor.cancel());
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

                    Button button;
                    if (isDummy) {
                        final String addButtonText = messageSource.getMessage("shareholder.add.button", null, locale);
                        button = new AddButton(
                                addButtonText,
                                addButtonText,
                                e -> {
                                    if (editor.isOpen()) {
                                        editor.cancel();
                                    }
                                    editor.editItem(shareholder);
                                });
                    } else {
                        final String editButtonText = messageSource.getMessage("global.edit", null, locale);
                        button = new EditButton(
                                editButtonText,
                                e -> {
                                    if (editor.isOpen()) {
                                        editor.cancel();
                                    }
                                    editor.editItem(shareholder);
                                });
                    }

                    actions.add(button);

                    if (!isDummy) {
                        final DeleteButton deleteButton = createDeleteShareholderButton(messageSource, locale, shareholder);
                        actions.add(deleteButton);
                    }

                    return actions;
                }).setHeader(messageSource.getMessage("shareholders.table.actions", null, locale))
                .setAutoWidth(true);
    }

    private DeleteButton createDeleteShareholderButton(
            final MessageSource messageSource,
            final Locale locale,
            final Shareholder shareholder
    ) {
        return new DeleteButton(
                messageSource.getMessage("global.delete", null, locale),
                e -> {
                    final ConfirmDialog dialog = createDeleteShareholderDialog(messageSource, locale, shareholder);
                    dialog.open();
                }
        );
    }

    private ConfirmDialog createDeleteShareholderDialog(
            final MessageSource messageSource,
            final Locale locale,
            final Shareholder shareholder
    ) {
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

    private void setupLastNameField(
            final MessageSource messageSource,
            final Locale locale,
            final Binder<Shareholder> binder
    ) {
        final TextField lastNameField = new TextField();
        lastNameField.setWidthFull();
        binder.forField(lastNameField)
                .asRequired(messageSource.getMessage("shareholder.last-name.required", null, locale))
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
                .asRequired(messageSource.getMessage("shareholder.first-name.required", null, locale))
                .bind(Shareholder::getFirstName, Shareholder::setFirstName);

        this.addColumn(Shareholder::getFirstName)
                .setHeader(messageSource.getMessage("shareholders.table.first-name", null, locale))
                .setKey("firstName")
                .setAutoWidth(true);

        this.getColumnByKey("firstName").setEditorComponent(firstNameField);
    }

    private void setupDisplayIdColumn(
            final MessageSource messageSource,
            final Locale locale,
            final Binder<Shareholder> binder
    ) {
        final IntegerField displayIdField = new IntegerField();
        displayIdField.setWidthFull();

        this.addColumn(shareholder -> shareholder.getId() == null ? "" : String.valueOf(shareholder.getDisplayId()))
                .setHeader(messageSource.getMessage("shareholders.table.display_id", null, locale))
                .setKey("displayId")
                .setAutoWidth(true);

        binder.forField(displayIdField)
                .bind(Shareholder::getDisplayId, Shareholder::setDisplayId);

        this.getColumnByKey("displayId").setEditorComponent(displayIdField);
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
