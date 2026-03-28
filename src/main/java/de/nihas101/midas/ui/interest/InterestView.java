package de.nihas101.midas.ui.interest;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
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
import com.vaadin.flow.router.Route;
import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.entity.Source;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.interest.InterestCalculation;
import de.nihas101.midas.interest.dto.InterestRate;
import de.nihas101.midas.interest.service.InterestBookingsReader;
import de.nihas101.midas.interest.service.InterestBookingsService;
import de.nihas101.midas.interest.service.InterestBookingsWriter;
import de.nihas101.midas.interest.service.InterestRateService;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.bookings.BookingRow;
import de.nihas101.midas.ui.bookings.BookingsToBookingRowConverter;
import de.nihas101.midas.ui.common.MidasView;
import de.nihas101.midas.ui.common.ShareholderPicker;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import io.micrometer.common.util.StringUtils;
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
import java.util.Optional;
import java.util.stream.IntStream;

// TODO: Align the display of the table more like in the printed sheet
@Slf4j
@Route("interest-calculation")
@PageTitle("Interest Calculation")
public class InterestView extends MidasView implements BeforeEnterObserver {

    public static final VaadinIcon icon = VaadinIcon.BOOK_PERCENT;

    private final ShareholdersService shareholdersService;
    private final InterestBookingsWriter bookingsWriter;
    private final InterestBookingsReader bookingsReader;
    private final InterestRateService interestRateService;
    private final MessageSource messageSource;
    private ComboBox<Shareholder> shareholderPicker; // TODO: Store the selected shareholder somewhere so that we can use that in the same filter when switching views
    private ComboBox<Integer> yearPicker; // TODO: Store the selected shareholder somewhere so that we can use that in the same filter when switching views
    private BigDecimalField interestRateField;
    private HorizontalLayout headerActionRow;
    private HorizontalLayout footerActionRow;
    private Grid<InterestCalculationRow> interestCalculationGrid;
    private Checkbox updateInterestAutomaticallyToggle;

    public InterestView(
            final ShareholdersService shareholdersService,
            final InterestBookingsService bookingsService,
            final InterestRateService interestRateService,
            final MidasConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver
    ) {
        super(config, userConfigService, messageSource, midasLocaleResolver);
        this.shareholdersService = shareholdersService;
        this.bookingsWriter = bookingsService;
        this.bookingsReader = bookingsService;
        this.interestRateService = interestRateService;
        this.messageSource = messageSource;

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        setupHeader(content);
        setupInterestGrid(content);
        setupFooter(content);

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

    // TODO: All of this is straight from BookingsView, extract that code into its own class
    private void setupHeader(final VerticalLayout content) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.END);

        shareholderPicker = new ShareholderPicker(
                messageSource.getMessage("bookings.shareholder", null, getLocale()),
                shareholdersService.shareholders(),
                e -> recalculateInterestForDisplay()
        );
        setupYearPicker();
        headerActionRow = createActionRow();
        headerActionRow.setVisible(false);

        header.add(shareholderPicker, yearPicker, headerActionRow);
        header.setFlexGrow(1, headerActionRow);
        content.add(header);
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
        yearPicker.addValueChangeListener(e -> recalculateInterestForDisplay());
    }

    private HorizontalLayout createActionRow() {
        // TODO: Extract this into its own class, so we always set the locale
        interestRateField = new BigDecimalField(messageSource.getMessage("interest.rate.label", null, getLocale()));
        interestRateField.setMaxWidth("5em");
        interestRateField.setLocale(getLocale());
        interestRateField.setSuffixComponent(new Span("%"));
        interestRateField.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                recalculateInterest();
            }
        });

        final HorizontalLayout actions = new HorizontalLayout();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.add(interestRateField);
        actions.setWidthFull();

        return actions;
    }

    // TODO: This and recalculateInterestForDisplay duplicate a lot of code and logic
    private void recalculateInterest() {
        final Shareholder shareholder = shareholderPicker.getValue();
        final Integer yearValue = yearPicker.getValue();
        final BigDecimal rate = interestRateField.getValue();
        if (shareholder == null || yearValue == null) {
            return;
        }

        final Year year = Year.of(yearValue);
        // TODO: Updating of these two fields should be handled in a transaction
        final InterestRate interestRate = updateInterestRate(shareholder, yearValue, rate);
        final Bookings bookings = bookingsReader.interestRelatedBookingsForShareholderAndYear(shareholder.getId(), year);
        final InterestCalculation interestCalculation = new InterestCalculation(
                bookings,
                year,
                interestRate
        );

        if (Boolean.TRUE.equals(updateInterestAutomaticallyToggle.getValue())) {
            updateInterestBooking(shareholder, year, interestCalculation);
        }

        interestCalculationGrid.setItems(
                createRows(
                        yearValue,
                        bookings,
                        rate,
                        interestCalculation
                )
        );
    }

    private void updateInterestBooking(
            final Shareholder shareholder,
            final Year year,
            final InterestCalculation interestCalculation
    ) {
        final Booking booking = bookingsReader.systemGeneratedInterestForShareholderAndYear(
                shareholder,
                year
        );
        // TODO: Extract this logic into the service. On interest update -> trigger
        if (booking != null) {
            // TODO: This mutates the object! Handle this differently
            booking.setAmount(interestCalculation.interest());
            bookingsWriter.update(booking);
        } else {
            final Booking newBooking = new Booking(
                    null,
                    null,
                    shareholder.getId(),
                    year.atMonth(Month.DECEMBER).atEndOfMonth(),
                    BookingType.INTEREST,
                    interestCalculation.interest(),
                    messageSource.getMessage("bookings.type.interest", null, getLocale()),
                    Source.SYSTEM
            );
            bookingsWriter.create(newBooking);
        }
    }

    private InterestRate updateInterestRate(final Shareholder shareholder, final Integer year, final BigDecimal rate) {
        InterestRate interestRate = interestRateService.interestRate(shareholder.getId(), Year.of(year));
        if (interestRate != null) {
            interestRate.setInterestRate(rate); // TODO: This mutates the object! Do it differently
            interestRateService.update(interestRate);
        } else {
            interestRate = new InterestRate(null, shareholder.getId(), rate, Year.of(year));
            interestRateService.create(interestRate);
        }
        return interestRate;
    }

    private void recalculateInterestForDisplay() {
        final Shareholder shareholder = shareholderPicker.getValue();
        final Integer yearValue = yearPicker.getValue();

        final boolean hasSelection = shareholder != null && yearValue != null;
        headerActionRow.setVisible(hasSelection);
        footerActionRow.setVisible(hasSelection);

        if (!hasSelection) {
            interestRateField.setValue(null);
            interestCalculationGrid.setItems(new ArrayList<>());
            return;
        }

        final Year year = Year.of(yearValue);
        final BigDecimal interestRate = interestRate(shareholder, year).getInterestRate();
        interestRateField.setValue(interestRate);

        // TODO: An exists check is enough
        final Booking booking = bookingsReader.systemGeneratedInterestForShareholderAndYear(shareholder, year);
        updateInterestAutomaticallyToggle.setValue(booking != null);

        final Bookings bookings = bookingsReader.interestRelatedBookingsForShareholderAndYear(shareholder.getId(), year);
        final InterestCalculation interestCalculation = new InterestCalculation(
                bookings,
                year,
                interestRate
        );
        interestCalculationGrid.setItems(
                createRows(
                        yearValue,
                        bookings,
                        interestRate,
                        interestCalculation
                )
        );
    }

    private InterestRate interestRate(final Shareholder shareholder, final Year year) {
        InterestRate rate = interestRateService.interestRate(shareholder.getId(), year);
        if (rate == null) {
            return new InterestRate(null, shareholder.getId(), BigDecimal.ZERO, year);
        }
        return rate;
    }

    // TODO: Introduce classes for all this logic
    private List<InterestCalculationRow> createRows(
            final Integer year,
            final Bookings bookings,
            final BigDecimal interestRate,
            final InterestCalculation interestCalculation
    ) {
        final List<InterestCalculationRow> rows = new ArrayList<>();
        rows.add(
                new OpeningBalanceInterestCalculationRow(
                        bookings,
                        Year.of(year),
                        interestRate
                )
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
                        new InterestRow(interestCalculation.interest()),
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

    private void setupFooter(final VerticalLayout content) {
        footerActionRow = new HorizontalLayout();
        footerActionRow.setWidthFull();
        footerActionRow.setAlignItems(FlexComponent.Alignment.END);
        footerActionRow.setVisible(false);

        updateInterestAutomaticallyToggle = new Checkbox(
                messageSource.getMessage("interest.update.automatically.toggle.label", null, getLocale()),
                false,
                (HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Checkbox, Boolean>>) event -> {
                    if (!event.isFromClient()) {
                        return;
                    }

                    if (Boolean.TRUE.equals(event.getValue())) {
                        recalculateInterest();
                    } else {
                        bookingsWriter.deleteInterestBooking(shareholderPicker.getValue(), Year.of(yearPicker.getValue()));
                    }
                });

        footerActionRow.add(updateInterestAutomaticallyToggle);

        content.add(footerActionRow);
    }

    public static Icon icon() {
        return icon.create();
    }
}
