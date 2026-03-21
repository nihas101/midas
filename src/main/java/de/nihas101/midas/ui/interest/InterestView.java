package de.nihas101.midas.ui.interest;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.monthlytotal.MonthlyTotalSum;
import de.nihas101.midas.bookings.service.BookingsService;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.interest.InterestCalculation;
import de.nihas101.midas.interest.dto.InterestRate;
import de.nihas101.midas.interest.interestamount.Interest;
import de.nihas101.midas.interest.service.InterestRateService;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.bookings.BookingRow;
import de.nihas101.midas.ui.bookings.BookingsToBookingRowConverter;
import de.nihas101.midas.ui.common.MidasPage;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@Route("interest-calculation")
@PageTitle("Interest Calculation")
public class InterestView extends MidasPage {

    private final ShareholdersService shareholdersService;
    private final BookingsService bookingsService;
    private final InterestRateService interestRateService;
    private final MessageSource messageSource;
    private ComboBox<Shareholder> shareholderPicker; // TODO: Store the selected shareholder somewhere so that we can use that in the same filter when switching views
    private ComboBox<Integer> yearPicker; // TODO: Store the selected shareholder somewhere so that we can use that in the same filter when switching views
    private BigDecimalField interestRateField;
    private HorizontalLayout actionRow;
    private Grid<InterestCalculationRow> interestCalculationGrid;

    public InterestView(
            final ShareholdersService shareholdersService,
            final BookingsService bookingsService,
            final InterestRateService interestRateService,
            final MidasConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver
    ) {
        super(config, userConfigService, messageSource, midasLocaleResolver);
        this.shareholdersService = shareholdersService;
        this.bookingsService = bookingsService;
        this.interestRateService = interestRateService;
        this.messageSource = messageSource;

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        setupHeader(content);
        setupInterestGrid(content);

        setContent(content);
    }

    // TODO: All of this is straight from BookingsView, extract that code into its own class
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

    private HorizontalLayout createActionRow() {
        // TODO: Extract this into its own class, so we always set the locale
        interestRateField = new BigDecimalField(messageSource.getMessage("interest.rate.label", null, getLocale()));
        interestRateField.setLocale(getLocale());
        interestRateField.setSuffixComponent(new Span("%"));
        interestRateField.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                saveInterestRate();
            }
        });

        final HorizontalLayout actions = new HorizontalLayout();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.add(interestRateField);
        actions.setWidthFull();

        return actions;
    }

    private void saveInterestRate() {
        final Shareholder shareholder = shareholderPicker.getValue();
        final Integer year = yearPicker.getValue(); // TODO: Turn into year
        final BigDecimal rate = interestRateField.getValue();
        if (shareholder == null || year == null) {
            return;
        }

        final InterestRate interestRate = interestRateService.interestRate(shareholder.getId(), Year.of(year));
        if (interestRate != null) {
            interestRate.setInterestRate(rate); // TODO: This mutates the object! Do it differently
            interestRateService.update(interestRate);
        } else {
            interestRateService.create(new InterestRate(null, shareholder.getId(), rate, Year.of(year)));
        }
        refreshGrid();
    }

    private void refreshGrid() {
        final Shareholder shareholder = shareholderPicker.getValue();
        final Integer year = yearPicker.getValue(); // TODO: Wrap in java Year immediately!

        final boolean hasSelection = shareholder != null && year != null;
        actionRow.setVisible(hasSelection);

        if (!hasSelection) {
            interestRateField.setValue(null);
            interestCalculationGrid.setItems(new ArrayList<>());
            return;
        }

        final InterestRate rate = interestRateService.interestRate(shareholder.getId(), Year.of(year));
        // TODO: This mutates the object! Do it differently
        if (rate != null) {
            interestRateField.setValue(rate.getInterestRate());
        } else {
            interestRateField.setValue(BigDecimal.ZERO);
        }

        final Bookings bookings = bookingsService.bookingsForShareholderAndYear(shareholder.getId(), year);
        interestCalculationGrid.setItems(createRows(year, bookings));
    }

    // TODO: Introduce classes for all this logic
    private List<InterestCalculationRow> createRows(final Integer year, final Bookings bookings) {
        final List<InterestCalculationRow> rows = new ArrayList<>();
        final BigDecimal interestRate = interestRateField.getValue();
        rows.add(
                new OpeningBalanceInterestCalculationRow(
                        bookings,
                        Year.of(year),
                        interestRate
                )
        );

        final InterestCalculation interestCalculation = new InterestCalculation(
                bookings,
                year,
                interestRate
        );

        final List<InterestCalculationRow> interestCalculationRows = interestRows(
                year,
                bookings,
                interestCalculation
        );
        rows.addAll(interestCalculationRows);

        rows.addAll(
                List.of(
                        new ZinszahlSumRow(interestCalculation.interestSum()),
                        new DivisorRow(interestCalculation.divisor()),
                        new InterestRow(interestCalculation.interest()), // TODO: This should be persisted in the bookings
                        new FinalSumRow(
                                Year.of(year).atMonth(Month.DECEMBER).atEndOfMonth(),
                                interestCalculation.finalSum()
                        )
                )
        );
        return rows;
    }

    // TODO: Decouple this from the bookings view classes
    private List<InterestCalculationRow> interestRows(
            final Integer year, // TODO: Use Year class!
            final Bookings bookings,
            final InterestCalculation interestCalculation
    ) {
        final List<InterestCalculationRow> rows = new ArrayList<>();
        MoneyAmount currentBalance = bookings.openingBalance().getOpeningBalance();

        for (Month month : Month.values()) {
            // TODO: This should not depend on the booking row
            final List<BookingRow> bookingRows = new BookingsToBookingRowConverter(bookings, month, currentBalance).bookingRows();

            if (!bookingRows.isEmpty()) {
                // The last row of the month has the correct balance at the end of the month
                currentBalance = bookingRows.getLast().balance();

                rows.add(
                        new DefaultInterestCalculationRow(
                                YearMonth.of(year, month),
                                interestCalculation.interests().get(month),
                                getLocale(),
                                interestCalculation.monthlyBalances().get(month),
                                interestCalculation.monthlyTotalSums().get(month)
                        )
                );
            }
        }
        return rows;
    }

    // TODO: Hide zeros (check how the booking view does it)
    // TODO: Move to own class (same in bookings view)
    private void setupInterestGrid(final VerticalLayout content) {
        interestCalculationGrid = new Grid<>();
        interestCalculationGrid.setWidthFull();

        // TODO: Null safety! (Also create a wrapper for InterestCalculationRow that handles all the fallbacks and stuff in the lambdas here)
        setupColumn(interestCalculationGrid.addColumn(InterestCalculationRow::monthAsString), "Monat", ColumnTextAlign.START);
        // TODO: These header wit S/H were displayed slightly different in the old program output, investigate how best to display that
        //       Probably involves a div or span
        setupColumn(
                interestCalculationGrid.addColumn(
                        i -> Optional.ofNullable(i)
                                .map(InterestCalculationRow::totalTransaction)
                                .map(Transaction::moneyAmount)
                                .map(m -> m.format(getLocale()))
                                .orElse("")
                ), "Bewegungen", ColumnTextAlign.END); // TODO: i18n
        setupColumn(
                interestCalculationGrid.addColumn(
                        i -> Optional.ofNullable(i)
                                .map(InterestCalculationRow::totalTransaction)
                                .map(Transaction::type)
                                .map(TransactionType::getValue)
                                .orElse("")
                ), "S/H", ColumnTextAlign.START); // TODO: i18n
        setupColumn(interestCalculationGrid.addColumn(
                i -> formatAmount(
                        Optional.ofNullable(i)
                                .map(InterestCalculationRow::balanceAtEndOfMonth)
                                .map(Transaction::moneyAmount)
                                .orElse(null)
                )
        ), "Saldo", ColumnTextAlign.END); // TODO: i18n
        setupColumn(interestCalculationGrid.addColumn(
                i -> Optional.ofNullable(i)
                        .map(InterestCalculationRow::balanceAtEndOfMonth)
                        .map(Transaction::type)
                        .map(TransactionType::getValue)
                        .orElse("")
        ), "S/H", ColumnTextAlign.START); // TODO: i18n
        setupColumn(interestCalculationGrid.addColumn(
                i -> formatDays(
                        Optional.ofNullable(i)
                                .map(InterestCalculationRow::interestDaysCount)
                                .orElse(null)
                )
        ), "Zinstage", ColumnTextAlign.CENTER); // TODO: i18n
        // TODO: We need to format the comma for the divisor in the summary only here -> Create a wrapper for this that handles this and only wrap the divisor
        setupColumn(interestCalculationGrid.addColumn(
                i -> formatInterestAmounts(
                        Optional.ofNullable(i)
                                .map(InterestCalculationRow::interestAmount)
                                .orElse(null)
                )
        ), "Zinszahl", ColumnTextAlign.CENTER); // TODO: i18n

        content.add(interestCalculationGrid);
    }

    private String formatInterestAmounts(final BigDecimal interestAmounts) {
        // To display empty cells for empty amounts
        return interestAmounts == null ? "" : interestAmounts.toString();
    }

    private String formatDays(final Integer days) {
        // To display empty cells for empty amounts
        return days == null ? "" : days.toString();
    }

    private String formatAmount(final MoneyAmount amount) {
        // To display empty cells for empty amounts
        return amount == null || amount.equals(MoneyAmount.ZERO) ? "" : amount.format(getLocale());
    }

    // TODO: Extract into common class for bookings view and this?
    private void setupColumn(
            final Grid.Column<?> column,
            final String headerName,
            final ColumnTextAlign columnTextAlign
    ) {
        final Span header = new Span(headerName); // TODO: i18n
        header.getElement().setAttribute("part", "header-cell-content"); // To allow common header styling

        column.setAutoWidth(true)
                .setFrozen(true)
                .setResizable(true)
                .setTextAlign(columnTextAlign)
                .setHeader(header);
    }

}
