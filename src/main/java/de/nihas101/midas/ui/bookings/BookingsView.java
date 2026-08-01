package de.nihas101.midas.ui.bookings;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
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
import de.nihas101.midas.bookings.row.BookingRow;
import de.nihas101.midas.bookings.row.BookingRowService;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.bookings.service.BookingsService;
import de.nihas101.midas.bookings.service.BookingsWriter;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.export.ExportFactory;
import de.nihas101.midas.interest.service.bookingupdate.InterestUpdatingBookingsService;
import de.nihas101.midas.interest.service.openingbalanceupdate.InterestUpdatingOpeningBalanceService;
import de.nihas101.midas.lock.ShareholderLock;
import de.nihas101.midas.lock.service.LockService;
import de.nihas101.midas.lock.service.LockWriter;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.openingbalance.service.OpeningBalanceService;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.common.AddButton;
import de.nihas101.midas.ui.common.DeleteButton;
import de.nihas101.midas.ui.common.EditButton;
import de.nihas101.midas.ui.common.HeaderActionBar;
import de.nihas101.midas.ui.common.MidasView;
import de.nihas101.midas.ui.common.QueryParameter;
import de.nihas101.midas.ui.common.ShareholderPicker;
import de.nihas101.midas.ui.common.YearPicker;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.ui.common.DownloadTrigger;
import de.nihas101.midas.ui.interest.InterestView;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;

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
    private final BookingRowService bookingRowService;
    private final LockWriter lockWriter;
    private final ShareholderLock shareholderLock;
    private final ExportFactory exportFactory;

    private Checkbox updateNextYearsBalanceAutomaticallyToggle;
    private BigDecimalField openingBalanceField;
    private HorizontalLayout actionRow;
    private Grid<BookingRow> grid;
    private HeaderActionBar headerActionBar;
    private DownloadTrigger downloadTrigger;

    public BookingsView(
            final ShareholdersService shareholdersService,
            final BookingsService bookingsReader,
            final InterestUpdatingBookingsService bookingsWriter,
            final InterestUpdatingOpeningBalanceService openingBalanceService,
            final MidasConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver,
            final BookingRowService bookingRowService,
            final LockService lockWriter,
            final ShareholderLock shareholderLock,
            final ExportFactory exportFactory
    ) {
        super(config, userConfigService, messageSource, midasLocaleResolver);
        this.shareholdersService = shareholdersService;
        this.bookingsReader = bookingsReader;
        this.bookingsWriter = bookingsWriter;
        this.openingBalanceService = openingBalanceService;
        this.messageSource = messageSource;
        this.bookingRowService = bookingRowService;
        this.lockWriter = lockWriter;
        this.shareholderLock = shareholderLock;
        this.exportFactory = exportFactory;

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        content.add(new H2(messageSource.getMessage("bookings", null, getLocale())));

        this.downloadTrigger = new DownloadTrigger(content);

        setupHeader(content);
        setupGrid(content);

        setContent(content);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // TODO: Move this logic into the query parameter?
        event.getLocation().getQueryParameters().getSingleParameter(QueryParameter.QUERY_PARAM_SHAREHOLDER)
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
                        headerActionBar.setSelectedShareholder(shareholder);
                    } catch (NumberFormatException e) {
                        log.warn("Unparsable shareholderId in query parameter: {}. Ignoring parameter.", shareholderId);
                    }
                });
        event.getLocation().getQueryParameters().getSingleParameter(QueryParameter.QUERY_PARAM_YEAR)
                .ifPresent(year -> {
                    if (StringUtils.isBlank(year)) {
                        return;
                    }
                    try {
                        headerActionBar.setSelectedYear(Integer.parseInt(year));
                    } catch (NumberFormatException e) {
                        log.warn("Unparsable year in query parameter: {}. Ignoring parameter.", year);
                    }
                });
    }

    private void setupHeader(final VerticalLayout content) {
        final Locale locale = getLocale();

        actionRow = createActionRow(locale);
        final Class<BookingsView> viewClass = BookingsView.class;
        final Runnable onUpdate = this::refreshGridForInitialDisplay;
        headerActionBar = new HeaderActionBar(
                messageSource,
                locale,
                new ShareholderPicker(
                        messageSource,
                        locale,
                        shareholdersService,
                        QueryParameter.shareholderParameter(
                                viewClass,
                                onUpdate
                        )
                ),
                new YearPicker(
                        messageSource,
                        locale,
                        QueryParameter.yearParameter(
                                viewClass,
                                onUpdate
                        ),
                        getMidasConfig()
                ),
                actionRow,
                shareholderLock,
                lockWriter,
                onUpdate,
                downloadTrigger,
                exportFactory,
                Set.of("bookings")
        );
        content.add(headerActionBar);
    }

    private HorizontalLayout createActionRow(final Locale locale) {
        openingBalanceField = new BigDecimalField(messageSource.getMessage("bookings.type.opening-balance", null, locale));
        openingBalanceField.setMaxWidth("9em");
        openingBalanceField.setLocale(locale);
        openingBalanceField.setSuffixComponent(new Span("€")); // TODO: Use currency from properties
        openingBalanceField.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                saveOpeningBalance();
            }
        });

        // TODO: When implementing the locking of years, check beforehand if the year is locked and if so, grey this check box out with a tooltip on why and what do to to re-enable it

        updateNextYearsBalanceAutomaticallyToggle = new Checkbox(messageSource.getMessage("booking.update.automatically.toggle.label", null, locale));
        updateNextYearsBalanceAutomaticallyToggle.addValueChangeListener(e -> {
            if (!e.isFromClient()) {
                return;
            }

            final Shareholder shareholder = headerActionBar.getSelectedShareholder();
            final Year nextYear = Year.of(headerActionBar.getSelectedYear().getValue()).plusYears(1);

            final OpeningBalance nextYearsOpeningBalance = openingBalanceService.openingBalance(
                    shareholder.getId(),
                    nextYear
            );
            if (nextYearsOpeningBalance != null && nextYearsOpeningBalance.getSource() == Source.USER) {
                final ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setHeader(messageSource.getMessage("bookings.dialog.opening-balance-exists.warning.title", null, locale));
                confirmDialog.setText(
                        messageSource.getMessage(
                                "bookings.dialog.opening-balance-exists.warning.message",
                                new Object[]{
                                        shareholder.getFirstName() + " " + shareholder.getLastName(),
                                        nextYearsOpeningBalance.getOpeningBalance().format(locale) + "€", // TODO: Define currency in properties
                                        nextYear
                                },
                                locale
                        )
                );
                confirmDialog.setCancelable(true);
                confirmDialog.setCancelText(messageSource.getMessage("global.cancel", null, locale));
                confirmDialog.setConfirmText(messageSource.getMessage("bookings.dialog.opening-balance-exists.warning.confirm", null, locale));
                confirmDialog.addConfirmListener(confirmEvent -> refreshGrid());
                confirmDialog.addCancelListener(cancelEvent -> e.getSource().setValue(e.getOldValue()));
                confirmDialog.open();
            } else {
                refreshGrid();
            }
        });

        final String addBookingMessage = messageSource.getMessage("bookings.add-booking", null, locale);
        final AddButton addBookingButton = new AddButton(
                addBookingMessage,
                addBookingMessage,
                e -> {
                    final BookingFormDialog bookingFormDialog = new BookingFormDialog(
                            shareholdersService,
                            bookingsReader,
                            bookingsWriter,
                            messageSource,
                            locale,
                            headerActionBar.getSelectedShareholder(),
                            booking -> refreshGrid(),
                            this.getMidasConfig().getUi()
                    );
                    bookingFormDialog.open();
                }
        );

        final HorizontalLayout actions = new HorizontalLayout();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.setAlignItems(FlexComponent.Alignment.BASELINE);
        actions.add(updateNextYearsBalanceAutomaticallyToggle, openingBalanceField, addBookingButton);
        actions.setWidthFull();

        return actions;
    }

    private void saveOpeningBalance() {
        final Shareholder shareholder = headerActionBar.getSelectedShareholder();
        final Year year = headerActionBar.getSelectedYear();
        if (shareholder == null || year == null) {
            return;
        }

        final BigDecimal amount = openingBalanceField.getValue();
        final OpeningBalance openingBalance = openingBalanceService.openingBalance(shareholder.getId(), year);
        if (openingBalance == null) {
            openingBalanceService.create(
                    new OpeningBalance(
                            null,
                            shareholder.getId(),
                            MoneyAmount.of(amount),
                            year,
                            Source.USER
                    )
            );
        } else {
            openingBalance.setOpeningBalance(MoneyAmount.of(amount));
            // The user may overwrite the system generated value
            // at that point we shouldn't consider it system generated anymore
            openingBalance.setSource(Source.USER);
            openingBalanceService.update(openingBalance);
        }
        refreshGrid();
    }

    private void setupGrid(final VerticalLayout content) {
        grid = new Grid<>();
        grid.setSizeFull();
        grid.setEmptyStateText(messageSource.getMessage("bookings.table.empty-state-text", null, getLocale()));
        grid.setPartNameGenerator(BookingRow::partName);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);

        setupColumn(grid.addColumn(BookingRow::displayId), "bookings.table.id", ColumnTextAlign.START);
        setupColumn(grid.addColumn(BookingRow::dateStr), "bookings.table.date", ColumnTextAlign.START);
        setupColumn(grid.addColumn(BookingRow::comment), "bookings.table.comment", ColumnTextAlign.START);

        final Grid.Column<BookingRow> totalColumn = grid.addColumn(r -> formatAmount(r.total()));
        totalColumn.setPartNameGenerator(r -> "separator-column");
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

            final Shareholder currentShareholder = headerActionBar.getSelectedShareholder();
            final Year currentYear = headerActionBar.getSelectedYear();
            final boolean isLocked = currentShareholder != null
                    && currentYear != null
                    && shareholderLock.isLocked(currentShareholder, currentYear);

            for (final Booking booking : row.bookings()) {
                final HorizontalLayout actionRow = new HorizontalLayout();
                actionRow.setPadding(false);
                actionRow.setSpacing(true);

                final EditButton editButton = createEditBookingButton(booking, isLocked);
                final DeleteButton deleteButton = createDeleteBookingButton(booking, isLocked);

                actionRow.add(editButton, deleteButton);
                actionsContainer.add(actionRow);
            }
            return actionsContainer;
        }).setHeader(messageSource.getMessage("shareholders.table.actions", null, getLocale())).setAutoWidth(true);

        content.add(grid);

        // Header parts for vertical separators
        grid.getHeaderRows().getFirst().getCell(totalColumn).setPartName("separator-column");
        grid.getHeaderRows().getFirst().getCell(balanceColumn).setPartName("balance-column");
    }

    private EditButton createEditBookingButton(final Booking booking, final boolean isLocked) {
        final EditButton editButton = new EditButton(
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
                        headerActionBar.getSelectedShareholder(),
                        booking,
                        b -> refreshGrid(),
                        this.getMidasConfig().getUi()
                ).open();
            }
        });
        editButton.setEnabled(!isLocked);
        return editButton;
    }

    private DeleteButton createDeleteBookingButton(final Booking booking, final boolean isLocked) {
        final DeleteButton deleteButton = new DeleteButton(
                messageSource.getMessage("global.delete", null, getLocale()),
                e -> {
                    final ConfirmDialog dialog = createDeleteBookingDialog(booking);
                    dialog.open();
                });
        deleteButton.addThemeVariants(LUMO_ERROR);
        deleteButton.setEnabled(!isLocked);
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
        column.setPartNameGenerator(r -> "separator-column");
        setupColumn(column, bookingType.getI18nKey(), ColumnTextAlign.END);
        grid.getHeaderRows().getFirst().getCell(column).setPartName("separator-column");
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

    private void refreshGridForInitialDisplay() {
        final Shareholder shareholder = headerActionBar.getSelectedShareholder();
        final Year year = headerActionBar.getSelectedYear();

        final boolean hasSelection = shareholder != null && year != null;
        headerActionBar.setActionButtonsVisible(hasSelection);

        if (!hasSelection) {
            openingBalanceField.setValue(null);
            grid.setItems(new ArrayList<>());
            return;
        }

        final boolean isLocked = shareholderLock.isLocked(shareholder, year);
        applyLockState(shareholder, year, isLocked);

        final OpeningBalance openingBalance = openingBalanceService.openingBalance(shareholder.getId(), year);
        if (openingBalance != null) {
            openingBalanceField.setValue(openingBalance.getOpeningBalance().toBigDecimalForInput());
        } else {
            openingBalanceField.setValue(ZERO);
        }

        final OpeningBalance nextYearsOpeningBalance = openingBalanceService.openingBalance(shareholder.getId(), year.plusYears(1));
        final boolean shouldUpdateNextYearsBalance = nextYearsOpeningBalance != null && nextYearsOpeningBalance.getSource() == Source.SYSTEM;
        updateNextYearsBalanceAutomaticallyToggle.setValue(shouldUpdateNextYearsBalance);

        final Bookings bookings = bookingsReader.bookingsForShareholderAndYear(shareholder.getId(), year);
        if (bookings.isEmpty()) {
            grid.setItems(emptyList());
            return;
        }

        grid.setItems(bookingRowService.generateRows(bookings, getLocale()));
    }

    private void refreshGrid() {
        final Shareholder shareholder = headerActionBar.getSelectedShareholder();
        final Year year = headerActionBar.getSelectedYear();

        final boolean hasSelection = shareholder != null && year != null;
        headerActionBar.setActionButtonsVisible(hasSelection);

        if (!hasSelection) {
            openingBalanceField.setValue(null);
            grid.setItems(new ArrayList<>());
            return;
        }

        final boolean isLocked = shareholderLock.isLocked(shareholder, year);
        applyLockState(shareholder, year, isLocked);

        final OpeningBalance openingBalance = openingBalanceService.openingBalance(shareholder.getId(), year);
        if (openingBalance != null) {
            openingBalanceField.setValue(openingBalance.getOpeningBalance().toBigDecimalForInput());
        } else {
            openingBalanceField.setValue(ZERO);
        }

        final Bookings bookings = bookingsReader.bookingsForShareholderAndYear(shareholder.getId(), year);
        if (bookings.isEmpty()) {
            if (!isLocked) {
                updateNextYearsBalanceIfNeeded(shareholder, year, emptyList());
            }
            grid.setItems(emptyList());
            return;
        }
        // TODO: We currently mix two responsibilities here, the creation of the display rows and the logic for calculating them,
        //       we should separate those concerns in the future, so that updateNextYearsBalance does not depend on ui related
        //       classes for business logic
        final List<BookingRow> bookingRows = bookingRowService.generateRows(bookings, getLocale());
        if (!isLocked) {
            updateNextYearsBalanceIfNeeded(shareholder, year, bookingRows);
        }
        grid.setItems(bookingRows);
    }

    private void applyLockState(
            final Shareholder shareholder,
            final Year year,
            final boolean isLocked
    ) {
        if (isLocked) {
            headerActionBar.lockLockUnlockButton();
        } else {
            headerActionBar.unlockLockUnlockButton();
        }

        // Disable interactive edit elements when locked
        openingBalanceField.setReadOnly(isLocked);

        // Disable auto-update toggle: locked current year OR locked next year prevents updating
        final boolean nextYearLocked = shareholderLock.isLocked(shareholder, year.plusYears(1));
        final boolean toggleDisabled = isLocked || nextYearLocked;
        updateNextYearsBalanceAutomaticallyToggle.setEnabled(!toggleDisabled);
        if (nextYearLocked && !isLocked) {
            updateNextYearsBalanceAutomaticallyToggle.setTooltipText(
                    messageSource.getMessage(
                            "bookings.lock.tooltip.next-year-locked",
                            null,
                            getLocale()
                    )
            );
        } else {
            updateNextYearsBalanceAutomaticallyToggle.setTooltipText(null);
        }

        // Disable add booking button when locked
        actionRow.getChildren()
                .filter(c -> c instanceof AddButton)
                .map(c -> (AddButton) c)
                .forEach(c -> c.setEnabled(!isLocked));
    }

    private void updateNextYearsBalanceIfNeeded(
            final Shareholder shareholder,
            final Year year,
            final List<BookingRow> bookings
    ) {
        final OpeningBalance nextYearsOpeningBalance = openingBalanceService.openingBalance(
                shareholder.getId(),
                year.plusYears(1)
        );
        if (!Boolean.TRUE.equals(updateNextYearsBalanceAutomaticallyToggle.getValue())) {
            if (nextYearsOpeningBalance != null && nextYearsOpeningBalance.getSource() == Source.SYSTEM) {
                // Set the source (back) to user, so that we won't update it anymore in the future
                nextYearsOpeningBalance.setSource(Source.USER);
                openingBalanceService.update(nextYearsOpeningBalance);
            }
            return;
        }

        // We only update the balance for next year.
        // We DON'T trigger a chain here and recalculate all amounts of the next year,
        // which would mean we would need to update the balance for the following year as well and so on.
        final MoneyAmount nextYearsOpening = getNextYearsOpening(bookings);

        if (nextYearsOpeningBalance != null) {
            nextYearsOpeningBalance.setOpeningBalance(nextYearsOpening);
            // Possibly overwrites a user sources opening balance
            // so make sure to set the source
            nextYearsOpeningBalance.setSource(Source.SYSTEM);
            openingBalanceService.update(nextYearsOpeningBalance);
        } else {
            final OpeningBalance openingBalance = OpeningBalance.builder()
                    .shareholderId(headerActionBar.getSelectedShareholder().getId())
                    .openingBalance(nextYearsOpening)
                    .year(headerActionBar.getSelectedYear().plusYears(1))
                    .source(Source.SYSTEM)
                    .build();
            openingBalanceService.create(openingBalance);
        }
    }

    private MoneyAmount getNextYearsOpening(final List<BookingRow> bookings) {
        // TODO: This .getLast is a bad assumption, see comment in caller
        return bookings.isEmpty() ? MoneyAmount.ZERO : bookings.getLast().balance();
    }

    public static Icon icon() {
        return icon.create();
    }

}
