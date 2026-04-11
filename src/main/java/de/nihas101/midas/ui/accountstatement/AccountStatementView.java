package de.nihas101.midas.ui.accountstatement;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.common.MidasView;
import de.nihas101.midas.ui.common.ShareholderPicker;
import de.nihas101.midas.ui.common.YearPicker;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Route("account-statements")
@PageTitle("Account Statements")
public class AccountStatementView extends MidasView implements BeforeEnterObserver { // TODO

    public static final VaadinIcon icon = VaadinIcon.WALLET;

    private final ShareholdersService shareholdersService;
    private final AccountStatementService accountStatementService;
    private final MessageSource messageSource;
    private ComboBox<Shareholder> shareholderPicker;
    private ComboBox<Integer> yearPicker;
    private Grid<AccountStatementRow> accountStatementGrid;
    private Grid<AccountStatementRow> closingStatementGrid;

    public AccountStatementView(
            final ShareholdersService shareholdersService,
            final AccountStatementService accountStatementService,
            final MidasConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver
    ) {
        super(config, userConfigService, messageSource, midasLocaleResolver);
        this.shareholdersService = shareholdersService;
        this.accountStatementService = accountStatementService;
        this.messageSource = messageSource;

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        content.add(new H2(messageSource.getMessage("account-statements", null, getLocale())));

        setupHeader(content);
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
                    UI.getCurrent().navigate(AccountStatementView.class, queryParameters);
                    refreshContent();
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
                    UI.getCurrent().navigate(AccountStatementView.class, queryParameters);
                    refreshContent();
                }
        );

        header.add(shareholderPicker, yearPicker);
        content.add(header);
    }

    private void setupAccountStatementGrid(final VerticalLayout content) {
        accountStatementGrid = new Grid<>();
        accountStatementGrid.setEmptyStateText(messageSource.getMessage("bookings.table.empty-state-text", null, getLocale()));
        accountStatementGrid.setWidthFull();
        accountStatementGrid.setAllRowsVisible(true);
        accountStatementGrid.setPartNameGenerator(AccountStatementRow::partName);
        accountStatementGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        setupColumn(accountStatementGrid.addColumn(AccountStatementRow::displayId), "account-statements.table.id", ColumnTextAlign.START);
        setupColumn(accountStatementGrid.addColumn(AccountStatementRow::dateStr), "account-statements.table.date", ColumnTextAlign.START);
        setupColumn(
                accountStatementGrid.addColumn(asr -> asr.label(messageSource, getLocale())),
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

        content.add(accountStatementGrid);

        // Header parts for vertical separators
        final HeaderRow headerRow = accountStatementGrid.getHeaderRows().getFirst();
        headerRow.getCell(debitColumn).setPartName("separator-column");
        headerRow.getCell(creditColumn).setPartName("separator-column");
        headerRow.getCell(balanceColumn).setPartName("separator-column");
    }

    private void setupClosingStatementGrid(final VerticalLayout content) {
        content.setSpacing(false);
        closingStatementGrid = new Grid<>();
        closingStatementGrid.setWidthFull();
        closingStatementGrid.setAllRowsVisible(true);
        closingStatementGrid.setPartNameGenerator(AccountStatementRow::partName);
        closingStatementGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        final Grid.Column<AccountStatementRow> dateColumn = closingStatementGrid.addColumn(AccountStatementRow::dateStr);
        dateColumn.setWidth("75%");
        dateColumn.setTextAlign(ColumnTextAlign.END);
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

    private void refreshContent() {
        Shareholder shareholder = shareholderPicker.getValue();
        Integer yearValue = yearPicker.getValue();

        final boolean hasSelection = shareholder != null && yearValue != null;
        if (!hasSelection) {
            accountStatementGrid.setItems(new ArrayList<>());
            closingStatementGrid.setItems(new ArrayList<>());
            return;
        }

        final RunningTotalAccountStatements accountStatements = accountStatementService.runningTotalAccountStatements(shareholder, Year.of(yearValue));
        if (accountStatements.isEmpty()) {
            accountStatementGrid.setItems(Collections.emptyList());
            closingStatementGrid.setItems(Collections.emptyList());
            return;
        }

        accountStatementGrid.setItems(createRows(accountStatements));
        closingStatementGrid.setItems(createRow(accountStatements));
    }

    private List<AccountStatementRow> createRows(final RunningTotalAccountStatements accountStatements) {
        return accountStatements.runningTotalAccountStatements()
                .stream()
                .map(RunningTotalAccountStatementRow::new)
                .collect(Collectors.toList());
    }

    private AccountStatementRow createRow(final RunningTotalAccountStatements accountStatements) {
        return new ClosingAccountStatementRow(accountStatements);
    }

    public static Icon icon() {
        return icon.create();
    }
}
