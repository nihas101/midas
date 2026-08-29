package de.nihas101.midas.vaadin.ui.bookings;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingFactory;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.api.bookings.BookingsWriter;
import de.nihas101.midas.api.commenttemplate.CommentTemplatesReader;
import de.nihas101.midas.api.commenttemplate.CommentTemplatesWriter;
import de.nihas101.midas.api.interest.InterestUpdatingBookingsService;
import de.nihas101.midas.api.interest.InterestUpdatingOpeningBalanceService;
import de.nihas101.midas.api.lock.LockService;
import de.nihas101.midas.api.lock.LockWriter;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.api.openingbalance.OpeningBalanceFactory;
import de.nihas101.midas.api.openingbalance.OpeningBalanceService;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.userconfig.UserConfigFactory;
import de.nihas101.midas.api.userconfig.UserConfigService;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.bookings.row.BookingRow;
import de.nihas101.midas.core.bookings.row.BookingRowService;
import de.nihas101.midas.core.bookings.service.BookingsService;
import de.nihas101.midas.core.commenttemplate.service.CommentTemplatesService;
import de.nihas101.midas.core.config.CoreConfig;
import de.nihas101.midas.core.export.ExportFactory;
import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import de.nihas101.midas.vaadin.ui.commenttemplate.CommentTemplatesTable;
import de.nihas101.midas.vaadin.ui.common.AddButton;
import de.nihas101.midas.vaadin.ui.common.DeleteButton;
import de.nihas101.midas.vaadin.ui.common.DownloadTrigger;
import de.nihas101.midas.vaadin.ui.common.EditButton;
import de.nihas101.midas.vaadin.ui.common.Formatter;
import de.nihas101.midas.vaadin.ui.common.GridHelper;
import de.nihas101.midas.vaadin.ui.common.HeaderActionBar;
import de.nihas101.midas.vaadin.ui.common.MidasView;
import de.nihas101.midas.vaadin.ui.common.QueryParameter;
import de.nihas101.midas.vaadin.ui.common.ShareholderPicker;
import de.nihas101.midas.vaadin.ui.common.YearPicker;
import de.nihas101.midas.vaadin.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.vaadin.ui.interest.InterestView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
    private final CommentTemplatesReader commentTemplatesReader;
    private final CommentTemplatesWriter commentTemplatesWriter;
    private final OpeningBalanceService openingBalanceService;
    private final MessageSource messageSource;
    private final BookingRowService bookingRowService;
    private final LockWriter lockWriter;
    private final ShareholderLock shareholderLock;
    private final ExportFactory exportFactory;
    private final DownloadTrigger downloadTrigger;
    private final BookingFactory bookingFactory;
    private final OpeningBalanceFactory openingBalanceFactory;

    private Checkbox updateNextYearsBalanceAutomaticallyToggle;
    private BigDecimalField openingBalanceField;
    private HorizontalLayout actionRow;
    private Grid<BookingRow> grid;
    private HeaderActionBar headerActionBar;

    public BookingsView(
            final ShareholdersService shareholdersService,
            final BookingsService bookingsReader,
            final InterestUpdatingBookingsService bookingsWriter,
            final InterestUpdatingOpeningBalanceService openingBalanceService,
            final CommentTemplatesService commentTemplatesService,
            final CoreConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver,
            final BookingRowService bookingRowService,
            final LockService lockWriter,
            final ShareholderLock shareholderLock,
            final ExportFactory exportFactory,
            final BookingFactory bookingFactory,
            final OpeningBalanceFactory openingBalanceFactory,
            final UserConfigFactory userConfigFactory
    ) {
        super(
                config,
                userConfigService,
                messageSource,
                midasLocaleResolver,
                userConfigFactory
        );
        this.shareholdersService = shareholdersService;
        this.bookingsReader = bookingsReader;
        this.bookingsWriter = bookingsWriter;
        this.commentTemplatesReader = commentTemplatesService;
        this.commentTemplatesWriter = commentTemplatesService;
        this.openingBalanceService = openingBalanceService;
        this.messageSource = messageSource;
        this.bookingRowService = bookingRowService;
        this.lockWriter = lockWriter;
        this.shareholderLock = shareholderLock;
        this.exportFactory = exportFactory;
        this.bookingFactory = bookingFactory;
        this.openingBalanceFactory = openingBalanceFactory;

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        content.add(new H2(messageSource.getMessage("bookings", null, getLocale())));

        this.downloadTrigger = new DownloadTrigger(content);

        VerticalLayout bookingsTabContent = new VerticalLayout();
        bookingsTabContent.setSizeFull();
        setupHeader(bookingsTabContent);
        setupGrid(bookingsTabContent);

        CommentTemplatesTable commentTemplatesTable = new CommentTemplatesTable(
                commentTemplatesReader,
                commentTemplatesWriter,
                messageSource,
                getLocale()
        );

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.add(messageSource.getMessage("bookings", null, getLocale()), bookingsTabContent);
        tabSheet.add(messageSource.getMessage("comment-templates", null, getLocale()), commentTemplatesTable);

        content.add(tabSheet);

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
                Set.of(ExportViewName.BOOKINGS)
        );
        content.add(headerActionBar);
    }

    private HorizontalLayout createActionRow(final Locale locale) {
        openingBalanceField = new BigDecimalField(messageSource.getMessage("bookings.type.opening-balance", null, locale));
        openingBalanceField.setMaxWidth("9em");
        openingBalanceField.setLocale(locale);
        openingBalanceField.setSuffixComponent(new Span(getMidasConfig().getUi().getCurrencySymbol()));
        openingBalanceField.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                saveOpeningBalance();
            }
        });

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
                                        nextYearsOpeningBalance.getOpeningBalance().format(locale) + getMidasConfig().getUi().getCurrencySymbol(),
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
                            commentTemplatesReader,
                            messageSource,
                            locale,
                            headerActionBar.getSelectedShareholder(),
                            shareholderLock,
                            booking -> refreshGrid(),
                            this.getMidasConfig().getUi(),
                            bookingFactory
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
                    openingBalanceFactory.create(
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
        final Formatter formatter = this.getFormatter();
        final GridHelper gridHelper = this.getGridHelper();
        grid = gridHelper.createGrid(BookingRow::partName);

        gridHelper.setupColumn(grid.addColumn(BookingRow::displayId), "bookings.table.id", ColumnTextAlign.START);
        gridHelper.setupColumn(grid.addColumn(BookingRow::formattedDate), "bookings.table.date", ColumnTextAlign.START);
        gridHelper.setupColumn(grid.addColumn(BookingRow::comment), "bookings.table.comment", ColumnTextAlign.START);

        final Grid.Column<BookingRow> totalColumn = grid.addColumn(r -> formatter.formatAmount(r.total()));
        totalColumn.setPartNameGenerator(r -> "separator-column");
        gridHelper.setupColumn(totalColumn, "bookings.table.total", ColumnTextAlign.END);

        gridHelper.setupColumn(grid, BookingType.WITHDRAWAL);
        gridHelper.setupColumn(grid, BookingType.TAX_PREVIOUS_YEAR);
        gridHelper.setupColumn(grid, BookingType.TAX_CREDIT);
        gridHelper.setupColumn(grid, BookingType.INTEREST);
        gridHelper.setupColumn(grid, BookingType.COMPENSATION);

        final Grid.Column<BookingRow> balanceColumn = grid.addColumn(r -> formatter.formatAmount(r.balance()));
        balanceColumn.setPartNameGenerator(r -> "balance-column"); // Header part for no vertical separators
        gridHelper.setupColumn(balanceColumn, "bookings.table.balance", ColumnTextAlign.END);

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
                        commentTemplatesReader,
                        messageSource,
                        getLocale(),
                        headerActionBar.getSelectedShareholder(),
                        booking,
                        shareholderLock,
                        b -> refreshGrid(),
                        this.getMidasConfig().getUi(),
                        bookingFactory
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
                booking.getDate().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)),
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

        grid.setItems(bookingRowService.generateRows(bookings, getLocale(), year));
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
        final List<BookingRow> bookingRows = bookingRowService.generateRows(bookings, getLocale(), year);
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
            final OpeningBalance openingBalance = openingBalanceFactory.create(
                    headerActionBar.getSelectedShareholder().getId(),
                    nextYearsOpening,
                    headerActionBar.getSelectedYear().plusYears(1),
                    Source.SYSTEM
            );
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
