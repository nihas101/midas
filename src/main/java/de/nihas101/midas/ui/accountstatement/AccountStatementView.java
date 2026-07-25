package de.nihas101.midas.ui.accountstatement;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.accountstatement.row.AccountStatementRow;
import de.nihas101.midas.accountstatement.row.AccountStatementRowService;
import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.accountstatement.service.DefaultAccountStatementService;
import de.nihas101.midas.accountstatement.service.RunningTotalAccountStatementService;
import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.bookings.service.BookingsService;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.lock.ShareholderLock;
import de.nihas101.midas.lock.service.LockService;
import de.nihas101.midas.lock.service.LockWriter;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.bookings.BookingsView;
import de.nihas101.midas.ui.common.AddButton;
import de.nihas101.midas.ui.common.MidasView;
import de.nihas101.midas.ui.common.ShareholderPicker;
import de.nihas101.midas.ui.common.YearPicker;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.ui.common.lock.LockDialog;
import de.nihas101.midas.ui.common.lock.LockUnlockButton;
import de.nihas101.midas.ui.common.lock.UnlockDialog;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;

@Slf4j
@Route("account-statements")
@PageTitle("Account Statements")
public class AccountStatementView extends MidasView implements BeforeEnterObserver { // TODO: Add a toggle to carry forward the closing balance to next year

    public static final VaadinIcon icon = VaadinIcon.WALLET;

    private final ShareholdersService shareholdersService;
    private final DefaultAccountStatementService accountStatementService;
    private final RunningTotalAccountStatementService runningTotalAccountStatementService;
    private final MessageSource messageSource;
    private final AccountStatementRowService accountStatementRowService;
    private final BookingsReader bookingsReader;
    private final LockWriter lockWriter;
    private final ShareholderLock shareholderLock;

    private ComboBox<Shareholder> shareholderPicker;
    private ComboBox<Integer> yearPicker;
    private HorizontalLayout warningBanner;
    private Span warningText;
    private Grid<AccountStatementRow> accountStatementGrid;
    private Grid<AccountStatementRow> closingStatementGrid;
    private HorizontalLayout actionRow;
    private Checkbox displayHiddenEntriesCheckbox;
    private AccountStatementRow draggedRow;
    private List<AccountStatementRow> currentRows;
    private LockUnlockButton lockUnlockButton;

    public AccountStatementView(
            final ShareholdersService shareholdersService,
            final DefaultAccountStatementService accountStatementService,
            final RunningTotalAccountStatementService runningTotalAccountStatementService,
            final MidasConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver,
            final AccountStatementRowService accountStatementRowService,
            final BookingsService bookingsReader,
            final LockService lockWriter,
            final ShareholderLock shareholderLock
    ) {
        super(config, userConfigService, messageSource, midasLocaleResolver);
        this.shareholdersService = shareholdersService;
        this.accountStatementService = accountStatementService;
        this.runningTotalAccountStatementService = runningTotalAccountStatementService;
        this.messageSource = messageSource;
        this.accountStatementRowService = accountStatementRowService;
        this.bookingsReader = bookingsReader;
        this.lockWriter = lockWriter;
        this.shareholderLock = shareholderLock;

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        addClassName("account-statement-view");

        content.add(new H2(messageSource.getMessage("account-statements", null, getLocale())));

        setupHeader(content);
        setupWarningBanner(content);
        setupAccountStatementGrid(content);
        setupClosingStatementGrid(content);

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

        shareholderPicker = createShareholderPicker();
        yearPicker = createYearPicker();

        lockUnlockButton = new LockUnlockButton(
                messageSource,
                getLocale(),
                e -> onLockUnlockClicked()
        );
        lockUnlockButton.setVisible(false);

        actionRow = createActionRow();
        actionRow.setVisible(false);

        header.add(
                shareholderPicker,
                yearPicker,
                lockUnlockButton,
                actionRow
        );
        content.add(header);
    }

    private ShareholderPicker createShareholderPicker() {
        return new ShareholderPicker(
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
                    UI.getCurrent().navigate(AccountStatementView.class, queryParameters);
                    refreshContent();
                }
        );
    }

    private YearPicker createYearPicker() {
        return new YearPicker(
                messageSource.getMessage("bookings.year", null, getLocale()),
                e -> {
                    final Integer year = e.getValue();

                    QueryParameters queryParameters = UI.getCurrent().getActiveViewLocation().getQueryParameters();
                    if (year != null) {
                        queryParameters = queryParameters.merging(QUERY_PARAM_YEAR, String.valueOf(year));
                    } else {
                        queryParameters = queryParameters.excluding(QUERY_PARAM_YEAR);
                    }
                    UI.getCurrent().navigate(AccountStatementView.class, queryParameters);
                    refreshContent();
                },
                getMidasConfig()
        );
    }

    private void onLockUnlockClicked() {
        final Shareholder shareholder = shareholderPicker.getValue();
        final Integer yearValue = yearPicker.getValue();
        if (shareholder == null || yearValue == null) {
            return;
        }

        final Year year = Year.of(yearValue);
        lockUnlockDialog(
                shareholderLock.isLocked(shareholder, year),
                getLocale(),
                year,
                shareholder
        ).open();
    }

    private ConfirmDialog lockUnlockDialog(
            final boolean isCurrentlyLocked,
            final Locale locale,
            final Year year,
            final Shareholder shareholder
    ) {
        final ConfirmDialog dialog;
        if (isCurrentlyLocked) {
            dialog = new UnlockDialog(
                    messageSource,
                    locale,
                    year,
                    shareholder,
                    e -> {
                        lockWriter.unlock(shareholder, year);
                        lockUnlockButton.unlock();
                        refreshContent();
                    }
            );
        } else {
            dialog = new LockDialog(
                    messageSource,
                    locale,
                    year,
                    shareholder,
                    e -> {
                        lockWriter.lock(shareholder, year);
                        lockUnlockButton.lock();
                        refreshContent();
                    }
            );
        }
        return dialog;
    }

    private HorizontalLayout createActionRow() {
        final String displayHiddenEntriesMessage = messageSource.getMessage("account-statements.show-hidden", null, getLocale());
        displayHiddenEntriesCheckbox = new Checkbox(
                displayHiddenEntriesMessage,
                false,
                e -> refreshContent()
        );


        final String addEntryMessage = messageSource.getMessage("account-statements.add-manual-entry", null, getLocale());
        final Button addManualRowBtn = new AddButton(
                addEntryMessage,
                addEntryMessage,
                e -> {
                    final ManualRowDialog manualRowDialog = new ManualRowDialog(
                            messageSource,
                            accountStatementService,
                            shareholderPicker.getValue(),
                            Year.of(yearPicker.getValue()),
                            this::refreshContent,
                            getLocale()
                    );
                    manualRowDialog.open();
                }
        );

        final HorizontalLayout actions = new HorizontalLayout();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.setAlignItems(FlexComponent.Alignment.BASELINE);
        actions.add(displayHiddenEntriesCheckbox, addManualRowBtn);
        actions.setWidthFull();

        return actions;
    }

    private void setupWarningBanner(final VerticalLayout content) {
        warningBanner = new HorizontalLayout();
        warningBanner.setWidthFull();
        warningBanner.setVisible(false);
        warningBanner.setAlignItems(FlexComponent.Alignment.CENTER);
        warningBanner.getStyle().set("background-color", "var(--lumo-error-color-10pct)");
        warningBanner.getStyle().set("border", "1px solid var(--lumo-error-color-50pct)");
        warningBanner.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        warningBanner.getStyle().set("padding", "var(--lumo-space-m)");

        final Icon warningIcon = new Icon(VaadinIcon.WARNING);
        warningIcon.setColor("var(--lumo-error-color)");
        warningText = new Span();
        warningText.getStyle().set("color", "var(--lumo-error-text-color)");
        warningText.getStyle().set("font-weight", "bold");

        warningBanner.add(warningIcon, warningText);
        content.add(warningBanner);
    }

    private void setupAccountStatementGrid(final VerticalLayout content) {
        accountStatementGrid = new Grid<>();
        // Drag‑handle column – shows a grip icon for rows that can be moved
        // Opening‑balance rows get an empty placeholder so they cannot be dragged
        Grid.Column<AccountStatementRow> dragHandleColumn = accountStatementGrid.addComponentColumn(this::dragHandle);
        dragHandleColumn.setHeader("");
        dragHandleColumn.setFlexGrow(0);
        dragHandleColumn.setWidth("40px");
        dragHandleColumn.setResizable(false);

        accountStatementGrid.setEmptyStateText(messageSource.getMessage("bookings.table.empty-state-text", null, getLocale()));
        accountStatementGrid.setWidthFull();
        accountStatementGrid.setAllRowsVisible(true);
        accountStatementGrid.setPartNameGenerator(AccountStatementRow::partName);

        accountStatementGrid.setRowsDraggable(true);
        accountStatementGrid.setDropMode(GridDropMode.BETWEEN);

        accountStatementGrid.addDragStartListener(event -> draggedRow = event.getDraggedItems().getFirst());
        accountStatementGrid.addDragEndListener(event -> draggedRow = null);

        accountStatementGrid.addDropListener(event -> {
            final AccountStatementRow targetRow = event.getDropTargetItem().orElse(null);
            final GridDropLocation dropLocation = event.getDropLocation();

            if (draggedRow == null || targetRow == null || draggedRow.equals(targetRow)) {
                return;
            }

            if (draggedRow.isOpeningBalance() || targetRow.isOpeningBalance()) {
                return;
            }

            if (currentRows == null) {
                return;
            }

            final List<AccountStatementRow> items = new ArrayList<>(currentRows);
            items.remove(draggedRow);

            int targetIndex = items.indexOf(targetRow);
            if (targetIndex < 0) {
                return;
            }

            if (dropLocation == GridDropLocation.BELOW) {
                items.add(targetIndex + 1, draggedRow);
            } else {
                items.add(targetIndex, draggedRow);
            }

            final List<String> rowKeys = items.stream()
                    .filter(row -> !row.isOpeningBalance())
                    .map(AccountStatementRow::rowKey)
                    .toList();

            accountStatementService.saveOrder(
                    shareholderPicker.getValue(),
                    Year.of(yearPicker.getValue()),
                    rowKeys
            );

            refreshContent();
        });

        setupColumn(
                accountStatementGrid.addColumn(AccountStatementRow::displayId),
                "account-statements.table.id",
                ColumnTextAlign.START
        );
        setupColumn(
                accountStatementGrid.addColumn(AccountStatementRow::dateStr),
                "account-statements.table.date",
                ColumnTextAlign.START
        );
        setupColumn(
                accountStatementGrid.addColumn(AccountStatementRow::label),
                "account-statements.table.type",
                ColumnTextAlign.START
        );
        final Grid.Column<AccountStatementRow> debitColumn = accountStatementGrid.addColumn(
                accountStatementRow -> Optional.of(accountStatementRow)
                        .map(AccountStatementRow::debit)
                        .map(m -> m.format(getLocale()))
                        .orElse("")
        );
        debitColumn.setPartNameGenerator(r -> "separator-column");
        setupColumn(debitColumn, "account-statements.table.debit", ColumnTextAlign.END);

        final Grid.Column<AccountStatementRow> creditColumn = accountStatementGrid.addColumn(
                accountStatementRow -> Optional.of(accountStatementRow)
                        .map(AccountStatementRow::credit)
                        .map(m -> m.format(getLocale()))
                        .orElse("")
        );
        creditColumn.setPartNameGenerator(r -> "separator-column");
        setupColumn(creditColumn, "account-statements.table.credit", ColumnTextAlign.END);

        final Grid.Column<AccountStatementRow> balanceColumn = accountStatementGrid.addColumn(
                accountStatementRow -> Optional.of(accountStatementRow)
                        .map(AccountStatementRow::balance)
                        .map(m -> m.format(getLocale()))
                        .orElse("")
        );
        balanceColumn.setPartNameGenerator(r -> "separator-column");
        setupColumn(balanceColumn, "account-statements.table.balance", ColumnTextAlign.END);

        accountStatementGrid.addComponentColumn(this::accountStatementEditBar)
                .setHeader(
                        messageSource.getMessage(
                                "shareholders.table.actions",
                                null,
                                getLocale()
                        )
                ).setAutoWidth(true);

        content.add(accountStatementGrid);

        // Header parts for vertical separators
        final HeaderRow headerRow = accountStatementGrid.getHeaderRows().getFirst();
        headerRow.getCell(debitColumn).setPartName("separator-column");
        headerRow.getCell(creditColumn).setPartName("separator-column");
        headerRow.getCell(balanceColumn).setPartName("separator-column");
    }

    private Component dragHandle(final AccountStatementRow row) {
        final Shareholder currentShareholder = shareholderPicker.getValue();
        final Integer currentYear = yearPicker.getValue();
        final boolean isLocked = currentShareholder != null
                && currentYear != null
                && shareholderLock.isLocked(currentShareholder, Year.of(currentYear));

        if (row.isOpeningBalance() || isLocked) {
            return new Span();
        }
        final Icon icon = new Icon(VaadinIcon.MENU);
        icon.addClassName("drag-handle");
        return icon;
    }

    private HorizontalLayout accountStatementEditBar(final AccountStatementRow row) {
        final HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        final Shareholder shareholder = shareholderPicker.getValue();
        final Integer yearValue = yearPicker.getValue();
        final boolean isLocked = shareholder != null
                && yearValue != null
                && shareholderLock.isLocked(shareholder, Year.of(yearValue));
        if (row.isOpeningBalance()) {
            actions.add(openingBalanceEditButton(isLocked));
        } else {
            if (row.isManualExtra()) {
                actions.add(manualExtraEditButton(row, isLocked));
            } else {
                actions.add(hideButton(row, isLocked));
            }

            if (row.isManualExtra()) {
                actions.add(revertButton(row, isLocked));
            }
        }
        return actions;
    }

    private Button revertButton(final AccountStatementRow row, final boolean isLocked) {
        final Button button = new Button(new Icon(VaadinIcon.TRASH));
        button.setTooltipText(messageSource.getMessage("account-statements.delete", null, getLocale()));
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
        button.addClickListener(e -> {
            accountStatementService.deleteOverride(row.displayId());
            refreshContent();
        });
        button.setEnabled(!isLocked);
        return button;
    }

    private Button hideButton(final AccountStatementRow row, final boolean isLocked) {
        Button button;
        if (row.isHidden()) {
            button = new Button(VaadinIcon.EYE.create());
            button.setTooltipText(messageSource.getMessage("global.include", null, getLocale()));
            button.addClickListener(e -> toggleExclude(row, false));
        } else {
            button = new Button(VaadinIcon.EYE_SLASH.create());
            button.setTooltipText(messageSource.getMessage("global.exclude", null, getLocale()));
            button.addClickListener(e -> toggleExclude(row, true));
        }
        button.setEnabled(!isLocked);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        return button;
    }

    private Button manualExtraEditButton(final AccountStatementRow row, final boolean isLocked) {
        final Button button = new Button(new Icon(VaadinIcon.EDIT));
        button.setTooltipText(messageSource.getMessage("global.edit", null, getLocale()));
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        button.addClickListener(e -> openOverrideDialog(row));
        button.setEnabled(!isLocked);
        return button;
    }

    private Button openingBalanceEditButton(final boolean isLocked) {
        final Button button = new Button(new Icon(VaadinIcon.EDIT));
        button.setTooltipText(messageSource.getMessage("global.edit", null, getLocale()));
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        button.addClickListener(e -> {
            QueryParameters queryParameters = new QueryParameters(Map.of(
                    QUERY_PARAM_SHAREHOLDER, List.of(String.valueOf(shareholderPicker.getValue().getId())),
                    QUERY_PARAM_YEAR, List.of(String.valueOf(yearPicker.getValue()))
            ));
            UI.getCurrent().navigate(BookingsView.class, queryParameters);
        });
        button.setEnabled(!isLocked);
        return button;
    }

    private void setupClosingStatementGrid(final VerticalLayout content) {
        content.setSpacing(false);
        closingStatementGrid = new Grid<>();
        closingStatementGrid.setWidthFull();
        closingStatementGrid.setAllRowsVisible(true);
        closingStatementGrid.setPartNameGenerator(AccountStatementRow::partName);
        closingStatementGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);

        final Grid.Column<AccountStatementRow> labelColumn = closingStatementGrid.addColumn(AccountStatementRow::label);
        labelColumn.setWidth("75%");
        labelColumn.setTextAlign(ColumnTextAlign.END);
        final Grid.Column<AccountStatementRow> closingAmountColumn = closingStatementGrid.addColumn(
                accountStatementRow -> Optional.of(accountStatementRow)
                        .map(AccountStatementRow::balance)
                        .map(amount -> amount.format(getLocale()))
                        .orElse("")
        );
        closingAmountColumn.setWidth("25%");
        closingAmountColumn.setTextAlign(ColumnTextAlign.END);

        content.add(closingStatementGrid);
    }

    // TODO: Extract this into a common class between all views
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

    private void toggleExclude(final AccountStatementRow row, final boolean hidden) {
        accountStatementService.saveOverride(
                shareholderPicker.getValue(),
                Year.of(yearPicker.getValue()),
                row.bookingType(),
                row.amount(),
                hidden
        );
        refreshContent();
    }

    private void openOverrideDialog(final AccountStatementRow row) {
        final Dialog dialog = new Dialog();
        dialog.setHeaderTitle(messageSource.getMessage("bookings.dialog.title.edit", null, getLocale()));

        final VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        final TextField labelField = new TextField(
                messageSource.getMessage("bookings.type", null, getLocale())
        );
        labelField.setValue(row.label());
        labelField.setWidthFull();
        labelField.setReadOnly(!row.isManualExtra());

        final BigDecimalField amountField = new BigDecimalField(
                messageSource.getMessage("bookings.amount", null, getLocale())
        );
        amountField.setLocale(getLocale());
        amountField.setSuffixComponent(new Span("€")); // TODO: currency from config?
        amountField.setValue(row.amount().toBigDecimalForInput());
        amountField.setWidthFull();

        layout.add(labelField, amountField);
        dialog.add(layout);

        final Button saveBtn = new Button(
                messageSource.getMessage("global.save", null, getLocale()),
                e -> {
                    final BigDecimal val = amountField.getValue();
                    if (val == null) {
                        amountField.setErrorMessage(messageSource.getMessage("bookings.amount.error", null, getLocale()));
                        amountField.setInvalid(true);
                        return;
                    }
                    final MoneyAmount newAmount = MoneyAmount.of(val);
                    if (row.isManualExtra()) {
                        final String labelVal = labelField.getValue();
                        if (StringUtils.isBlank(labelVal)) {
                            labelField.setErrorMessage(messageSource.getMessage("shareholder.last-name.required", null, getLocale()));
                            labelField.setInvalid(true);
                            return;
                        }
                        accountStatementService.saveManualExtra(
                                row.displayId(),
                                shareholderPicker.getValue(),
                                Year.of(yearPicker.getValue()),
                                labelVal,
                                newAmount
                        );
                    } else {
                        accountStatementService.saveOverride(
                                shareholderPicker.getValue(),
                                Year.of(yearPicker.getValue()),
                                row.bookingType(),
                                newAmount,
                                false
                        );
                    }
                    dialog.close();
                    refreshContent();
                }
        );
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        final Button cancelBtn = new Button(messageSource.getMessage("global.cancel", null, getLocale()), e -> dialog.close());

        dialog.getFooter().add(saveBtn, cancelBtn);
        dialog.open();
    }

    private void refreshContent() {
        final Shareholder shareholder = shareholderPicker.getValue();
        final Integer yearValue = yearPicker.getValue();

        final boolean hasSelection = shareholder != null && yearValue != null;
        actionRow.setVisible(hasSelection);
        lockUnlockButton.setVisible(hasSelection);

        if (!hasSelection) {
            accountStatementGrid.setItems(new ArrayList<>());
            closingStatementGrid.setItems(new ArrayList<>());
            warningBanner.setVisible(false);
            return;
        }

        final Year year = Year.of(yearValue);
        final boolean isLocked = shareholderLock.isLocked(shareholder, year);
        applyLockState(isLocked);

        final RunningTotalAccountStatements accountStatements = runningTotalAccountStatementService.runningTotalAccountStatements(
                shareholder,
                year,
                messageSource,
                getLocale()
        );
        if (accountStatements.isEmpty()) {
            accountStatementGrid.setItems(emptyList());
            closingStatementGrid.setItems(emptyList());
            warningBanner.setVisible(false);
            return;
        }

        final List<AccountStatementRow> rows = accountStatementRowService.generateRows(accountStatements, displayHiddenEntriesCheckbox.getValue());
        this.currentRows = rows;
        accountStatementGrid.setItems(rows);
        closingStatementGrid.setItems(accountStatementRowService.generateClosingRow(accountStatements, getLocale()));
        checkForDivergence(shareholder, yearValue, rows);
    }

    private void applyLockState(final boolean isLocked) {
        if (isLocked) {
            lockUnlockButton.lock();
        } else {
            lockUnlockButton.unlock();
        }

        accountStatementGrid.setRowsDraggable(!isLocked);

        // Disable add booking button when locked
        actionRow.getChildren()
                .filter(c -> c instanceof AddButton)
                .map(c -> (AddButton) c)
                .forEach(c -> c.setEnabled(!isLocked));
    }

    private void checkForDivergence(
            final Shareholder shareholder,
            final Integer yearValue,
            final List<AccountStatementRow> rows
    ) {
        final Bookings bookings = bookingsReader.bookingsForShareholderAndYear(shareholder.getId(), Year.of(yearValue));
        final MoneyAmount bookingsSum = bookings.filter(b -> true).bookings()
                .stream()
                .map(Booking::getAmount)
                .reduce(MoneyAmount.ZERO, MoneyAmount::plus);

        final MoneyAmount typeRowsSum = rows.stream()
                .filter(row -> !row.isOpeningBalance())
                .filter(row -> !row.isHidden())
                .map(AccountStatementRow::amount)
                .reduce(MoneyAmount.ZERO, MoneyAmount::plus);

        if (!bookingsSum.equals(typeRowsSum)) {
            final MoneyAmount difference = typeRowsSum.minus(bookingsSum);
            final String formattedDiff = difference.abs().format(getLocale());
            final String formattedStatement = typeRowsSum.format(getLocale());
            final String formattedBookings = bookingsSum.format(getLocale());

            warningText.setText(messageSource.getMessage(
                    "account-statements.warning.divergent-message",
                    new Object[]{formattedDiff, formattedStatement, formattedBookings},
                    getLocale()
            ));
            warningBanner.setVisible(true);
        } else {
            warningBanner.setVisible(false);
        }
    }

    public static Icon icon() {
        return icon.create();
    }
}
