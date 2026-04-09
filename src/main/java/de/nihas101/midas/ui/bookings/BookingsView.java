package de.nihas101.midas.ui.bookings;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.entity.Source;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.bookings.service.BookingsService;
import de.nihas101.midas.bookings.service.BookingsWriter;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.interest.service.InterestUpdatingBookingsService;
import de.nihas101.midas.interest.service.InterestUpdatingOpeningBalanceService;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.openingbalance.service.OpeningBalanceService;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.common.AddButton;
import de.nihas101.midas.ui.common.DeleteButton;
import de.nihas101.midas.ui.common.EditButton;
import de.nihas101.midas.ui.common.MidasView;
import de.nihas101.midas.ui.common.ShareholderPicker;
import de.nihas101.midas.ui.common.YearPicker;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.ui.interest.InterestView;
import de.nihas101.midas.userconfig.service.UserConfigService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR;

@Slf4j
@Route("bookings")
@PageTitle("Bookings")
public class BookingsView extends MidasView implements BeforeEnterObserver {

    public static final VaadinIcon icon = VaadinIcon.BOOK_DOLLAR;

    private final ShareholdersService shareholdersService;
    private final BookingsReader bookingsReader;
    private final BookingsWriter bookingsWriter;
    private final OpeningBalanceService openingBalanceService;
    private final MessageSource messageSource;

    private ComboBox<Shareholder> shareholderPicker;
    private ComboBox<Integer> yearPicker;
    private BigDecimalField openingBalanceField;
    private HorizontalLayout actionRow;
    private Grid<BookingRow> grid;

    public BookingsView(
            final ShareholdersService shareholdersService,
            final BookingsService bookingsReader,
            final InterestUpdatingBookingsService bookingsWriter,
            final InterestUpdatingOpeningBalanceService openingBalanceService,
            final MidasConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver
    ) {
        super(config, userConfigService, messageSource, midasLocaleResolver);
        this.shareholdersService = shareholdersService;
        this.bookingsReader = bookingsReader;
        this.bookingsWriter = bookingsWriter;
        this.openingBalanceService = openingBalanceService;
        this.messageSource = messageSource;

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        content.add(new H2(messageSource.getMessage("bookings", null, getLocale())));

        setupHeader(content);
        setupGrid(content);

        setContent(content);
    }

    // TODO: Also add these to local storage
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getLocation().getQueryParameters().getSingleParameter(QUERY_PARAM_SHAREHOLDER)
                .ifPresent(shareholderId -> {
                    try {
                        if (StringUtils.isBlank(shareholderId)) {
                            return;
                        }
                        final Shareholder shareholder = shareholdersService.shareholder(Integer.parseInt(shareholderId));
                        if (shareholder == null) {
                            log.warn("Unknown shareholderId: {}. Ignoring parameter.", shareholderId);
                            return;
                        }
                        shareholderPicker.setValue(shareholder);
                    } catch (NumberFormatException e) {
                        log.warn("Unparsable shareholderId in query parameter: {}. Ignoring parameter.", shareholderId);
                    }
                });
        event.getLocation().getQueryParameters().getSingleParameter(QUERY_PARAM_YEAR)
                .ifPresent(year -> {
                    if (StringUtils.isBlank(year)) {
                        return;
                    }
                    try {
                        yearPicker.setValue(Integer.parseInt(year));
                    } catch (NumberFormatException e) {
                        log.warn("Unparsable year in query parameter: {}. Ignoring parameter.", year);
                    }
                });
    }

    private void setupHeader(final VerticalLayout content) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.END);

        shareholderPicker = new ShareholderPicker(
                messageSource.getMessage("bookings.shareholder", null, getLocale()),
                messageSource.getMessage("shareholder-picker.placeholder", null, getLocale()),
                shareholdersService,
                e -> {
                    final Shareholder shareholder = e.getValue();

                    QueryParameters queryParameters = UI.getCurrent().getActiveViewLocation().getQueryParameters();
                    if (shareholder != null) {
                        queryParameters = queryParameters.merging(QUERY_PARAM_SHAREHOLDER, String.valueOf(shareholder.getId()));
                    } else {
                        queryParameters = queryParameters.excluding(QUERY_PARAM_SHAREHOLDER);
                    }
                    UI.getCurrent().navigate(BookingsView.class, queryParameters);
                    refreshGrid();
                }
        );
        yearPicker = new YearPicker(
                messageSource.getMessage("bookings.year", null, getLocale()),
                e -> {
                    final Integer year = e.getValue();

                    QueryParameters queryParameters = UI.getCurrent().getActiveViewLocation().getQueryParameters();
                    if (year != null) {
                        queryParameters = queryParameters.merging(QUERY_PARAM_YEAR, String.valueOf(String.valueOf(year)));
                    } else {
                        queryParameters = queryParameters.excluding(QUERY_PARAM_YEAR);
                    }
                    UI.getCurrent().navigate(BookingsView.class, queryParameters);
                    refreshGrid();
                }
        );
        actionRow = createActionRow();
        actionRow.setVisible(false);

        header.add(shareholderPicker, yearPicker, actionRow);
        header.setFlexGrow(1, actionRow);
        content.add(header);
    }

    private HorizontalLayout createActionRow() {
        openingBalanceField = new BigDecimalField(messageSource.getMessage("bookings.type.opening-balance", null, getLocale()));
        openingBalanceField.setMaxWidth("9em");
        openingBalanceField.setLocale(getLocale());
        openingBalanceField.setSuffixComponent(new Span("€")); // TODO: Use currency from properties
        openingBalanceField.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                saveOpeningBalance();
            }
        });

        final String addBookingMessage = messageSource.getMessage("bookings.add-booking", null, getLocale());
        final AddButton addBookingButton = new AddButton(
                addBookingMessage,
                addBookingMessage,
                e -> {
                    final BookingFormDialog bookingFormDialog = new BookingFormDialog(
                            shareholdersService,
                            bookingsReader,
                            bookingsWriter,
                            messageSource,
                            getLocale(),
                            shareholderPicker.getValue(),
                            booking -> refreshGrid()
                    );
                    bookingFormDialog.open();
                }
        );

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

    private void setupGrid(final VerticalLayout content) {
        grid = new Grid<>();
        grid.setSizeFull();
        grid.setEmptyStateText(messageSource.getMessage("bookings.table.empty-state-text", null, getLocale()));
        grid.setPartNameGenerator(BookingRow::partName);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

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

                final EditButton editButton = createEditBookingButton(booking);
                final DeleteButton deleteButton = createDeleteBookingButton(booking);

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

    private EditButton createEditBookingButton(final Booking booking) {
        return new EditButton(
                messageSource.getMessage("global.edit", null, getLocale()), e -> {
            if (BookingType.INTEREST.equals(booking.getType()) && Source.SYSTEM == booking.getSource()) {
                final QueryParameters queryParameters = UI.getCurrent().getActiveViewLocation().getQueryParameters();
                UI.getCurrent().navigate(InterestView.class, queryParameters);
            } else {
                new BookingFormDialog(
                        shareholdersService,
                        bookingsReader,
                        bookingsWriter,
                        messageSource,
                        getLocale(),
                        shareholderPicker.getValue(),
                        booking,
                        b -> refreshGrid()
                ).open();
            }
        });
    }

    private DeleteButton createDeleteBookingButton(final Booking booking) {
        final DeleteButton deleteButton = new DeleteButton(
                messageSource.getMessage("global.delete", null, getLocale()),
                e -> {
                    final ConfirmDialog dialog = createDeleteBookingDialog(booking);
                    dialog.open();
                });
        deleteButton.addThemeVariants(LUMO_ERROR);
        return deleteButton;
    }

    private ConfirmDialog createDeleteBookingDialog(final Booking booking) {
        final ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(messageSource.getMessage("bookings.table.delete.confirmation.title", null, getLocale()));

        final String[] args = new String[]{
                messageSource.getMessage(booking.getType().getI18nKey(), null, getLocale()),
                booking.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), // TODO: Make format configurable
                booking.getAmount().format(getLocale())
        };
        dialog.setText(messageSource.getMessage("bookings.table.delete.confirmation.message", args, getLocale()));

        dialog.setCancelable(true);
        dialog.setCancelText(messageSource.getMessage("global.cancel", null, getLocale()));
        dialog.setConfirmText(messageSource.getMessage("global.delete", null, getLocale()));
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> {
            bookingsWriter.delete(booking);
            refreshGrid();
        });
        return dialog;
    }

    private String formatAmount(final MoneyAmount amount) {
        if (amount == null || amount.equals(MoneyAmount.ZERO)) {
            return ""; // To display empty cells for zero amounts
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
        Integer yearValue = yearPicker.getValue();

        final boolean hasSelection = shareholder != null && yearValue != null;
        actionRow.setVisible(hasSelection);

        if (!hasSelection) {
            openingBalanceField.setValue(null);
            grid.setItems(new ArrayList<>());
            return;
        }

        final Year year = Year.of(yearValue);
        OpeningBalance openingBalance = openingBalanceService.openingBalance(shareholder.getId(), year);
        if (openingBalance != null) {
            openingBalanceField.setValue(openingBalance.getOpeningBalance().toBigDecimalForInput());
        } else {
            openingBalanceField.setValue(BigDecimal.ZERO);
        }

        Bookings bookings = bookingsReader.bookingsForShareholderAndYear(shareholder.getId(), year);
        if (bookings.isEmpty()) {
            grid.setItems(Collections.emptyList());
            return;
        }
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
        final AtomicReference<MoneyAmount> currentBalance = new AtomicReference<>(
                bookings.openingBalance()
                        .getOpeningBalance()
        );

        final List<Month> months = Arrays.stream(Month.values())
                // TODO: Only calculate bookingsInMonth once and pass it to the BookingsToBookingRowConverter
                .filter(m -> !bookings.bookingsInMonth(m).bookings().isEmpty())
                .toList();
        months.stream()
                .limit(months.size() - 1)
                .forEach(month -> {
                    final CumulativeSummaryBookingRow summaryBookingRow = generateBookingRows(
                            bookings,
                            month,
                            currentBalance.get(),
                            rows,
                            "single-separator"
                    );
                    currentBalance.set(summaryBookingRow.balance());
                });

        generateBookingRows(bookings, months.getLast(), currentBalance.get(), rows, "double-separator");

        return rows;
    }

    private CumulativeSummaryBookingRow generateBookingRows(
            final Bookings bookings,
            final Month month,
            final MoneyAmount curr,
            final List<BookingRow> rows,
            final String partName
    ) {
        final CumulativeSummaryBookingRow cumulativeSummaryBookingRow = new CumulativeSummaryBookingRow(
                "",
                messageSource.getMessage("bookings.table.summary.cumulative", null, getLocale()),
                bookings,
                month,
                partName
        );

        new SummarizingBookingsToBookingRowConverter(
                new DefaultBookingsToBookingRowConverter(
                        bookings,
                        month,
                        curr,
                        rows::add
                ),
                new MonthlySummaryBookingRow(
                        messageSource.getMessage("bookings.table.summary.monthly", null, getLocale()),
                        bookings,
                        month
                ),
                cumulativeSummaryBookingRow,
                rows::add
        ).generate();

        return cumulativeSummaryBookingRow;
    }

    public static Icon icon() {
        return icon.create();
    }

}
