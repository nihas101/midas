package de.nihas101.midas.vaadin.ui.commenttemplate;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.nihas101.midas.api.commenttemplate.CommentTemplate;
import de.nihas101.midas.api.commenttemplate.CommentTemplatesReader;
import de.nihas101.midas.api.commenttemplate.CommentTemplatesWriter;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.vaadin.ui.common.AddButton;
import de.nihas101.midas.vaadin.ui.common.DeleteButton;
import de.nihas101.midas.vaadin.ui.common.EditButton;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR;

public class CommentTemplatesTable extends VerticalLayout {

    private final CommentTemplatesReader commentTemplatesReader;
    private final CommentTemplatesWriter commentTemplatesWriter;
    private final MessageSource messageSource;
    private final Locale locale;

    private final Grid<CommentTemplate> grid = new Grid<>();

    public CommentTemplatesTable(
            final CommentTemplatesReader commentTemplatesReader,
            final CommentTemplatesWriter commentTemplatesWriter,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this.commentTemplatesReader = commentTemplatesReader;
        this.commentTemplatesWriter = commentTemplatesWriter;
        this.messageSource = messageSource;
        this.locale = locale;

        setSizeFull();
        setPadding(false);

        setupTopBar();
        setupGrid();
        refresh();
    }

    private void setupTopBar() {
        final String addButtonText = messageSource.getMessage("comment-templates.add-button", null, locale);
        final AddButton addButton = new AddButton(
                addButtonText,
                addButtonText,
                e -> new CommentTemplateFormDialog(
                        commentTemplatesWriter,
                        messageSource,
                        locale,
                        null,
                        saved -> refresh()
                ).open()
        );

        final HorizontalLayout topBar = new HorizontalLayout(addButton);
        topBar.setWidthFull();
        topBar.setJustifyContentMode(JustifyContentMode.END);
        add(topBar);
    }

    private void setupGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);

        final Grid.Column<CommentTemplate> textColumn = grid.addColumn(CommentTemplate::getText);
        setupColumnHeader(textColumn, "comment-templates.text");

        final Grid.Column<CommentTemplate> typesColumn = grid.addColumn(template -> {
            if (template.getBookingTypes() == null || template.getBookingTypes().isEmpty()) {
                return messageSource.getMessage("comment-templates.types.all", null, locale);
            }
            return template.getBookingTypes().stream()
                    .map(t -> messageSource.getMessage(t.getI18nKey(), null, locale))
                    .collect(Collectors.joining(", "));
        });
        setupColumnHeader(typesColumn, "comment-templates.types");

        grid.addComponentColumn(template -> {
            final EditButton editButton = new EditButton(
                    messageSource.getMessage("global.edit", null, locale),
                    e -> new CommentTemplateFormDialog(
                            commentTemplatesWriter,
                            messageSource,
                            locale,
                            template,
                            saved -> refresh()
                    ).open()
            );

            final DeleteButton deleteButton = new DeleteButton(
                    messageSource.getMessage("global.delete", null, locale),
                    e -> createDeleteDialog(template).open()
            );
            deleteButton.addThemeVariants(LUMO_ERROR);

            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader(messageSource.getMessage("shareholders.table.actions", null, locale)).setAutoWidth(true);

        add(grid);
    }

    private void setupColumnHeader(final Grid.Column<?> column, final String i18nKey) {
        final Span header = new Span(messageSource.getMessage(i18nKey, null, locale));
        header.getElement().setAttribute("part", "header-cell-content");
        column.setAutoWidth(true).setResizable(true).setHeader(header);
    }

    private ConfirmDialog createDeleteDialog(final CommentTemplate template) {
        final ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(messageSource.getMessage("comment-templates.delete.confirmation.title", null, locale));
        dialog.setText(messageSource.getMessage("comment-templates.delete.confirmation.message", null, locale));
        dialog.setCancelable(true);
        dialog.setCancelText(messageSource.getMessage("global.cancel", null, locale));
        dialog.setConfirmText(messageSource.getMessage("global.delete", null, locale));
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> {
            commentTemplatesWriter.delete(template.getId());
            refresh();
        });
        return dialog;
    }

    public void refresh() {
        final List<? extends CommentTemplate> templates = commentTemplatesReader.getTemplates().toList();
        grid.setItems((List<CommentTemplate>) templates);
    }
}
