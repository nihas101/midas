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
import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.service.BookingsService;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.interest.dto.InterestRate;
import de.nihas101.midas.interest.service.InterestRateService;
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
import java.util.stream.IntStream;

@Slf4j
@Route("interest-calculation")
@PageTitle("Interest Calculation")
public class InterestView extends MidasPage {

    // TODO: Get rid of 'service' in these names, it adds nothing, maybe registry instead?
    private final ShareholdersService shareholdersService;
    private final BookingsService bookingsService;
    private final InterestRateService interestRateService;
    private final MessageSource messageSource;
    private ComboBox<Shareholder> shareholderPicker;
    private ComboBox<Integer> yearPicker;
    private BigDecimalField interestRateField;
    private HorizontalLayout actionRow;
    private Grid<InterestCalculationRow> grid;

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
        setupGrid(content);

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
        interestRateField = new BigDecimalField(messageSource.getMessage("interest.rate.label", null, getLocale()));
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
            grid.setItems(new ArrayList<>());
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
        grid.setItems(createRows(year, bookings));
    }

    // TODO: This is currently just a copy from bookings view, this needs to be its own thing
    private List<InterestCalculationRow> createRows(final Integer year, final Bookings bookings) {
        final List<InterestCalculationRow> rows = new ArrayList<>();
        rows.add(new OpeningBalanceInterestCalculationRow(bookings, Year.of(year), getLocale())); // TODO: Rename this for booking view and use own class here
        rows.addAll(monthlySummaryRows(year, bookings));
        return rows;
    }

    // TODO: Decouple this from the bookings view classes
    private List<InterestCalculationRow> monthlySummaryRows(final Integer year, final Bookings bookings) {
        final List<InterestCalculationRow> rows = new ArrayList<>();
        MoneyAmount currentBalance = bookings.openingBalance();

        for (Month month : Month.values()) {
            final List<BookingRow> bookingRows = new BookingsToBookingRowConverter(bookings, month, currentBalance).bookingRows();

            if (!bookingRows.isEmpty()) {
                //rows.addAll(bookingRows);
                // The last row of the month has the correct balance at the end of the month
                currentBalance = bookingRows.getLast().balance();

                rows.add(
                        new DefaultInterestCalculationRow(
                                bookings,
                                YearMonth.of(year, month),
                                getLocale()
                        )
                );
            }
        }
        return rows;
    }

    // TODO: Move to own class (same in bookings view)
    private void setupGrid(final VerticalLayout content) {
        grid = new Grid<>();
        grid.setSizeFull();

        setupColumn(grid.addColumn(InterestCalculationRow::monthAsString), "Monat", ColumnTextAlign.START);
        // TODO: These header wit S/H were displayed slightly different in the old program output, investigate how best to display that
        //       Probably involves a div or span
        setupColumn(grid.addColumn(InterestCalculationRow::totalTransactionAmountAsString), "Bewegungen S/H", ColumnTextAlign.END); // TODO: i18n
        setupColumn(grid.addColumn(InterestCalculationRow::balanceAtEndOfMonth), "Saldo S/H", ColumnTextAlign.END); // TODO: i18n
        setupColumn(grid.addColumn(InterestCalculationRow::interestDaysCount), "Zinstage", ColumnTextAlign.CENTER); // TODO: i18n
        setupColumn(grid.addColumn(InterestCalculationRow::interestAmount), "Zinszahl", ColumnTextAlign.CENTER); // TODO: i18n

        // TODO: Add Summe Zinszahl
        // TODO: Add Divisor
        // TODO: Add Zinsen
        // TODO: Add Bestand

        content.add(grid);
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
