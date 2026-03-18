package de.nihas101.midas.ui.bookings;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.service.BookingsService;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.openingbalance.service.OpeningBalanceService;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.common.MidasPage;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Route("bookings")
@PageTitle("Bookings")
public class BookingsView extends MidasPage {

    private final ShareholdersService shareholdersService;
    private final BookingsService bookingsService;
    private final OpeningBalanceService openingBalanceService;
    private final MessageSource messageSource;

    private ComboBox<Shareholder> shareholderPicker;
    private ComboBox<Integer> yearPicker;
    private BigDecimalField openingBalanceField;
    private HorizontalLayout actionRow;
    private Grid<BookingRow> grid;

    public BookingsView(
            final ShareholdersService shareholdersService,
            final BookingsService bookingsService,
            final OpeningBalanceService openingBalanceService,
            final MidasConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver
    ) {
        super(config, userConfigService, messageSource, midasLocaleResolver);
        this.shareholdersService = shareholdersService;
        this.bookingsService = bookingsService;
        this.openingBalanceService = openingBalanceService;
        this.messageSource = messageSource;

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        setupHeader(content);
        setupGrid(content);

        setContent(content);
    }

    private void setupHeader(final VerticalLayout content) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.END);

        setupShareholderPicker();
        setupYearPicker();
        actionRow = createActionRow();
        actionRow.setVisible(false);

        header.add(shareholderPicker, yearPicker, actionRow);
        header.setFlexGrow(1, actionRow);
        content.add(header);
    }

    private void setupShareholderPicker() {
        shareholderPicker = new ComboBox<>(messageSource.getMessage("bookings.shareholder", null, getLocale()));
        shareholderPicker.setItems(shareholdersService.shareholders().toList());
        shareholderPicker.setItemLabelGenerator(s -> s.getFirstName() + " " + s.getLastName() + " (" + s.getDisplayId() + ")");
        shareholderPicker.setPlaceholder("Search by name or ID..."); // TODO: i18n
        shareholderPicker.setClearButtonVisible(true);
        shareholderPicker.addValueChangeListener(e -> refreshGrid());
    }

    private void setupYearPicker() {
        LocalDate now = LocalDate.now(ZoneId.systemDefault());

        List<Integer> selectableYears = IntStream.rangeClosed(0, 99)
                .map(i -> now.getYear() - i)
                .boxed()
                .toList();
        yearPicker = new ComboBox<>(messageSource.getMessage("bookings.year", null, getLocale()), selectableYears);
        yearPicker.setValue(LocalDate.now().getYear());
        yearPicker.setWidth(6, Unit.EM);
        yearPicker.addValueChangeListener(e -> refreshGrid());
    }

    // TODO: Add element for editing saldo for this year
    private HorizontalLayout createActionRow() {
        openingBalanceField = new BigDecimalField(messageSource.getMessage("bookings.type.opening-balance", null, getLocale()));
        openingBalanceField.setLocale(getLocale());
        openingBalanceField.setSuffixComponent(new Span("€")); // TODO: Use currency from properties
        openingBalanceField.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                saveOpeningBalance();
            }
        });

        final Button addBookingButton = new Button(messageSource.getMessage("bookings.add-booking", null, getLocale()));
        addBookingButton.addClickListener(e -> {
            new BookingFormDialog(
                    shareholdersService,
                    bookingsService,
                    messageSource,
                    getLocale(),
                    shareholderPicker.getValue(),
                    booking -> refreshGrid()
            ).open();
        });

        final HorizontalLayout actions = new HorizontalLayout();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.setAlignItems(FlexComponent.Alignment.BASELINE);
        actions.add(openingBalanceField, addBookingButton);
        actions.setWidthFull();

        return actions;
    }

    private void saveOpeningBalance() {
        Shareholder shareholder = shareholderPicker.getValue();
        Integer year = yearPicker.getValue();
        if (shareholder == null || year == null) {
            return;
        }

        BigDecimal amount = openingBalanceField.getValue();
        final OpeningBalance openingBalance = openingBalanceService.openingBalance(shareholder.getId(), Year.of(year));
        if (openingBalance == null) {
            openingBalanceService.create(new OpeningBalance(null, shareholder.getId(), MoneyAmount.of(amount), Year.of(year)));
        } else {
            openingBalance.setOpeningBalance(MoneyAmount.of(amount));
            openingBalanceService.update(openingBalance);
        }
        refreshGrid();
    }

    // TODO: Add edit and delete buttons
    private void setupGrid(final VerticalLayout content) {
        grid = new Grid<>();
        grid.setSizeFull();

        grid.setPartNameGenerator(BookingRow::partName);

        setupColumn(grid.addColumn(BookingRow::displayId), "bookings.table.id", ColumnTextAlign.START);
        setupColumn(grid.addColumn(BookingRow::dateStr), "bookings.table.date", ColumnTextAlign.START);
        setupColumn(grid.addColumn(BookingRow::comment), "bookings.table.comment", ColumnTextAlign.START);

        final Grid.Column<BookingRow> totalColumn = grid.addColumn(r -> formatAmount(r.total()));
        // TODO: booking-type-column is used to add separators on the left and right of a cell
        totalColumn.setPartNameGenerator(r -> "booking-type-column"); // TODO: Think of a better name here, booking-type-column makes no sense here
        setupColumn(totalColumn, "bookings.table.total", ColumnTextAlign.END);

        setupColumn(BookingType.WITHDRAWAL);
        setupColumn(BookingType.TAX_PREVIOUS_YEAR);
        setupColumn(BookingType.TAX_CREDIT);
        setupColumn(BookingType.INTEREST);
        setupColumn(BookingType.COMPENSATION);

        final Grid.Column<BookingRow> balanceColumn = grid.addColumn(r -> formatAmount(r.balance()));
        balanceColumn.setPartNameGenerator(r -> "balance-column"); // Header part for no vertical separators
        setupColumn(balanceColumn, "bookings.table.balance", ColumnTextAlign.END);

        grid.addComponentColumn(row -> {
            final VerticalLayout actionsContainer = new VerticalLayout();
            actionsContainer.setPadding(false);
            actionsContainer.setSpacing(false);

            for (final Booking booking : row.bookings()) {
                final HorizontalLayout actionRow = new HorizontalLayout();
                actionRow.setPadding(false);
                actionRow.setSpacing(true);

                final Button editButton = createEditBookingButton(booking);
                final Button deleteButton = createDeleteBookingButton(booking);

                actionRow.add(editButton, deleteButton);
                actionsContainer.add(actionRow);
            }
            return actionsContainer;
        }).setHeader(messageSource.getMessage("shareholders.table.actions", null, getLocale())).setAutoWidth(true);

        content.add(grid);

        // Header parts for vertical separators
        grid.getHeaderRows().getFirst().getCell(totalColumn).setPartName("booking-type-column");
        grid.getHeaderRows().getFirst().getCell(balanceColumn).setPartName("balance-column");
    }

    private Button createEditBookingButton(final Booking booking) {
        final Button editButton = new Button(messageSource.getMessage("global.edit", null, getLocale()));
        editButton.addClickListener(e -> {
            new BookingFormDialog(
                    shareholdersService,
                    bookingsService,
                    messageSource,
                    getLocale(),
                    shareholderPicker.getValue(),
                    booking,
                    b -> refreshGrid()
            ).open();
        });
        return editButton;
    }

    private Button createDeleteBookingButton(final Booking booking) {
        final Button deleteButton = new Button(messageSource.getMessage("global.delete", null, getLocale()));
        deleteButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            final ConfirmDialog dialog = createDeleteBookingDialog(booking);
            dialog.open();
        });
        return deleteButton;
    }

    private ConfirmDialog createDeleteBookingDialog(final Booking booking) {
        final ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(messageSource.getMessage("bookings.table.delete.confirmation.title", null, getLocale()));

        final String[] args = new String[]{
                messageSource.getMessage(booking.getType().getI18nKey(), null, getLocale()),
                booking.getDate().toString(),
                booking.getAmount().format(getLocale())
        };
        dialog.setText(messageSource.getMessage("bookings.table.delete.confirmation.message", args, getLocale()));

        dialog.setCancelable(true);
        dialog.setCancelText(messageSource.getMessage("global.cancel", null, getLocale()));
        dialog.setConfirmText(messageSource.getMessage("global.delete", null, getLocale()));
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> {
            bookingsService.delete(booking);
            refreshGrid();
        });
        return dialog;
    }

    private String formatAmount(final MoneyAmount amount) {
        if (amount == null || amount.equals(MoneyAmount.ZERO)) {
            return ""; // To display empty cells for empty amounts
        }
        return amount.format(getLocale());
    }

    private void setupColumn(final BookingType bookingType) {
        final Grid.Column<BookingRow> column = grid.addColumn(r -> formatAmount(r.amount(bookingType)));
        column.setPartNameGenerator(r -> "booking-type-column");
        setupColumn(column, bookingType.getI18nKey(), ColumnTextAlign.END);
        grid.getHeaderRows().getFirst().getCell(column).setPartName("booking-type-column");
    }

    private void setupColumn(
            final Grid.Column<?> column,
            final String i18nKey,
            final ColumnTextAlign columnTextAlign
    ) {
        final Span header = new Span(messageSource.getMessage(i18nKey, null, getLocale()));
        header.getElement().setAttribute("part", "header-cell-content"); // To allow common header styling

        column.setAutoWidth(true)
                .setFrozen(true)
                .setResizable(true)
                .setTextAlign(columnTextAlign)
                .setHeader(header);
    }

    private void refreshGrid() {
        Shareholder shareholder = shareholderPicker.getValue();
        Integer year = yearPicker.getValue();

        final boolean hasSelection = shareholder != null && year != null;
        actionRow.setVisible(hasSelection);

        if (!hasSelection) {
            openingBalanceField.setValue(null);
            grid.setItems(new ArrayList<>());
            return;
        }

        OpeningBalance openingBalance = openingBalanceService.openingBalance(shareholder.getId(), Year.of(year));
        if (openingBalance != null) {
            openingBalanceField.setValue(openingBalance.getOpeningBalance().toBigDecimalForInput());
        } else {
            openingBalanceField.setValue(BigDecimal.ZERO);
        }

        Bookings bookings = bookingsService.bookingsForShareholderAndYear(shareholder.getId(), year);
        grid.setItems(createRows(bookings));
    }

    private List<BookingRow> createRows(final Bookings bookings) {
        List<BookingRow> rows = new ArrayList<>();
        rows.add(new OpeningBalanceBookingRow(bookings));
        rows.addAll(monthlySummaryRows(bookings));
        return rows;
    }

    private List<BookingRow> monthlySummaryRows(final Bookings bookings) {
        final List<BookingRow> rows = new ArrayList<>();
        MoneyAmount currentBalance = bookings.openingBalance();

        for (Month month : Month.values()) {
            final List<BookingRow> bookingRows = new BookingsToBookingRowConverter(bookings, month, currentBalance).bookingRows();

            if (!bookingRows.isEmpty()) {
                rows.addAll(bookingRows);
                // The last row of the month has the correct balance at the end of the month
                currentBalance = bookingRows.getLast().balance();

                rows.add(
                        new MonthlySummaryBookingRow(
                                messageSource.getMessage("bookings.table.summary.monthly", null, getLocale()),
                                bookings,
                                month
                        )
                );
                rows.add(
                        new CumulativeSummaryBookingRow(
                                "",
                                messageSource.getMessage("bookings.table.summary.cumulative", null, getLocale()),
                                bookings,
                                month
                        )
                );
            }
        }
        return rows;
    }

}
