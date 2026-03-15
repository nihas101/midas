package de.nihas101.midas.ui.interest;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.service.BookingsService;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.bookings.BookingFormDialog;
import de.nihas101.midas.ui.bookings.BookingRow;
import de.nihas101.midas.ui.bookings.BookingsToBookingRowConverter;
import de.nihas101.midas.ui.common.MidasPage;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

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
    private final MessageSource messageSource;
    private ComboBox<Shareholder> shareholderPicker;
    private ComboBox<Integer> yearPicker;
    private Grid<InterestCalculationRow> grid;

    public InterestView(
            final ShareholdersService shareholdersService,
            final BookingsService bookingsService,
            final MidasConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver
    ) {
        super(config, userConfigService, messageSource, midasLocaleResolver);
        this.shareholdersService = shareholdersService;
        this.bookingsService = bookingsService;
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
        final HorizontalLayout actionRow = createActionRow();

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
        // TODO: Replace with a way to enter the interest rate (Zinssatz)
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
        actions.add(addBookingButton);
        actions.setWidthFull();

        return actions;
    }

    private void refreshGrid() {
        Shareholder shareholder = shareholderPicker.getValue();
        Integer year = yearPicker.getValue(); // TODO: Wrap in java Year?

        if (shareholder == null || year == null) {
            grid.setItems(new ArrayList<>());
            return;
        }

        Bookings bookings = bookingsService.bookingsForShareholderAndYear(shareholder.getId(), year);
        grid.setItems(createRows(year, bookings));
    }

    // TODO: This is currently just a copy from bookings view, this needs to be its own thing
    private List<InterestCalculationRow> createRows(final Integer year, final Bookings bookings) {
        List<InterestCalculationRow> rows = new ArrayList<>();
        rows.add(new OpeningBalanceInterestCalculationRow(bookings, Year.of(year), getLocale())); // TODO: Rename this for booking view and use own class here
        rows.addAll(monthlySummaryRows(year, bookings));
        return rows;
    }

    // TODO: Decouple this from the bookings view classes
    private List<InterestCalculationRow> monthlySummaryRows(final Integer year, final Bookings bookings) {
        final List<InterestCalculationRow> rows = new ArrayList<>();
        MoneyAmount currentBalance = bookings.initialBalance();

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
