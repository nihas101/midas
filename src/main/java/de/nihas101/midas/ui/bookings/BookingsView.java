package de.nihas101.midas.ui.bookings;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.service.BookingsService;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.common.MidasPage;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Route("bookings")
@PageTitle("Bookings")
public class BookingsView extends MidasPage {

    private final ShareholdersService shareholdersService;
    private final BookingsService bookingsService;
    private final MessageSource messageSource;
    private final Locale locale;

    private ComboBox<Shareholder> shareholderPicker;
    private ComboBox<Integer> yearPicker;
    private Grid<BookingRow> grid;

    public BookingsView(
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
        this.locale = midasLocaleResolver.resolve();

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        setupHeader(content);
        setupGrid(content);

        setContent(content);
    }

    private void setupHeader(final VerticalLayout content) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();

        setupShareholderPicker();
        setupYearPicker();

        header.add(shareholderPicker, yearPicker);
        content.add(header);
    }

    private void setupShareholderPicker() {
        shareholderPicker = new ComboBox<>(messageSource.getMessage("bookings.shareholder", null, locale));
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
        yearPicker = new ComboBox<>(messageSource.getMessage("bookings.year", null, locale), selectableYears);
        yearPicker.setValue(LocalDate.now().getYear());
        yearPicker.setWidth(6, Unit.EM);
        yearPicker.addValueChangeListener(e -> refreshGrid());
    }

    private void setupGrid(final VerticalLayout content) {
        grid = new Grid<>();
        grid.setSizeFull();

        setupColumn(grid.addColumn(BookingRow::displayId), "bookings.table.id");
        setupColumn(grid.addColumn(BookingRow::dateStr), "bookings.table.date");
        setupColumn(grid.addColumn(BookingRow::comment), "bookings.table.comment");

        setupColumn(BookingType.WITHDRAWAL);
        setupColumn(BookingType.TAX_PREVIOUS_YEAR);
        setupColumn(BookingType.TAX_CREDIT);
        setupColumn(BookingType.INTEREST);
        setupColumn(BookingType.COMPENSATION);

        setupColumn(grid.addColumn(r1 -> r1.total().format(locale)), "bookings.table.total");
        setupColumn(grid.addColumn(r -> r.balance().format(locale)), "bookings.table.balance");

        content.add(grid);
    }

    private void setupColumn(final BookingType bookingType) {
        setupColumn(grid.addColumn(r -> r.amount(bookingType).format(locale)), bookingType.getI18nKey());
    }

    private void setupColumn(final Grid.Column<BookingRow> column, final String i18nKey) {
        column.setAutoWidth(true)
                .setFrozen(true)
                .setResizable(true)
                .setHeader(messageSource.getMessage(i18nKey, null, locale));
    }

    private void refreshGrid() {
        Shareholder shareholder = shareholderPicker.getValue();
        Integer year = yearPicker.getValue();

        if (shareholder == null || year == null) {
            grid.setItems(new ArrayList<>());
            return;
        }

        Bookings bookings = bookingsService.bookingsForShareholderAndYear(shareholder.getId(), year);
        grid.setItems(createRows(bookings));
    }

    private List<BookingRow> createRows(final Bookings bookings) {
        List<BookingRow> rows = new ArrayList<>();
        rows.add(initialBalanceRow(bookings));
        rows.addAll(monthlySummaryRows(bookings));
        return rows;
    }

    private OpeningBalanceRow initialBalanceRow(final Bookings bookings) {
        return new OpeningBalanceRow(
                messageSource.getMessage(BookingType.OPENING_BALANCE.getI18nKey(), null, locale),
                bookings
        );
    }

    private List<BookingRow> monthlySummaryRows(final Bookings bookings) {
        final List<BookingRow> rows = new ArrayList<>();
        for (Month month : Month.values()) {
            if (month.getValue() > LocalDate.now().getMonthValue() && yearPicker.getValue() >= LocalDate.now().getYear()) {
                continue;
            }
            final List<BookingRow> bookingRows = new BookingsToBookingRowConverter(bookings, month).bookingRows();
            if (bookingRows.isEmpty()) {
                continue;
            }
            rows.addAll(bookingRows);
            rows.add(
                    new MonthlySummaryBookingRow(
                            month.getDisplayName(TextStyle.FULL, locale),
                            messageSource.getMessage("bookings.table.summary.monthly", null, locale),
                            bookings,
                            month
                    )
            );
        }
        return rows;
    }

}
