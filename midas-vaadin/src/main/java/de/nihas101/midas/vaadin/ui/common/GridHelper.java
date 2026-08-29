package de.nihas101.midas.vaadin.ui.common;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.function.SerializableFunction;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.row.BookingRow;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class GridHelper {
    private final MessageSource messageSource;
    private final Locale locale;
    private final Formatter formatter;

    public GridHelper(
            final MessageSource messageSource,
            final Locale locale,
            final Formatter formatter
    ) {
        this.messageSource = messageSource;
        this.locale = locale;
        this.formatter = formatter;
    }

    public <T> Grid<T> createGrid(final SerializableFunction<T, String> partName) {
        final Grid<T> grid = new Grid<>();
        grid.setSizeFull();
        grid.setWidthFull();
        grid.setEmptyStateText(messageSource.getMessage("bookings.table.empty-state-text", null, locale));
        grid.setPartNameGenerator(partName);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_COMPACT);
        return grid;
    }

    public void setupColumn(
            final Grid<BookingRow> grid,
            final BookingType bookingType
    ) {
        final Grid.Column<BookingRow> column = grid.addColumn(r -> formatter.formatAmount(r.amount(bookingType)));
        column.setPartNameGenerator(r -> "separator-column");
        setupColumn(column, bookingType.getI18nKey(), ColumnTextAlign.END);
        grid.getHeaderRows().getFirst().getCell(column).setPartName("separator-column");
    }

    public void setupColumn(
            final Grid.Column<?> column,
            final String i18nKey,
            final ColumnTextAlign columnTextAlign
    ) {
        final Span header = new Span(messageSource.getMessage(i18nKey, null, locale));
        header.getElement().setAttribute("part", "header-cell-content"); // To allow common header styling

        column.setAutoWidth(true)
                .setFrozen(true)
                .setResizable(true)
                .setTextAlign(columnTextAlign)
                .setHeader(header);
    }
}