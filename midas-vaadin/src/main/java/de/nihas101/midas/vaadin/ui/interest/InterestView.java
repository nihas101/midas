package de.nihas101.midas.vaadin.ui.interest;

import com.vaadin.flow.component.checkbox.Checkbox;
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
import com.vaadin.flow.router.Route;
import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.BookingFactory;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.interest.InterestBookingsReader;
import de.nihas101.midas.api.interest.InterestBookingsService;
import de.nihas101.midas.api.interest.InterestBookingsWriter;
import de.nihas101.midas.api.interest.InterestCalculation;
import de.nihas101.midas.api.interest.InterestCalculationFactory;
import de.nihas101.midas.api.interest.InterestCalculationRow;
import de.nihas101.midas.api.interest.InterestRowService;
import de.nihas101.midas.api.interest.Transaction;
import de.nihas101.midas.api.interest.TransactionType;
import de.nihas101.midas.api.lock.LockService;
import de.nihas101.midas.api.lock.LockWriter;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.userconfig.UserConfigFactory;
import de.nihas101.midas.api.userconfig.UserConfigService;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.config.CoreConfig;
import de.nihas101.midas.core.export.ExportFactory;
import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.interest.DefaultInterestCalculation;
import de.nihas101.midas.core.interest.dto.InterestRate;
import de.nihas101.midas.core.interest.service.InterestRateService;
import de.nihas101.midas.core.lock.ShareholderLock;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import de.nihas101.midas.vaadin.ui.common.DownloadTrigger;
import de.nihas101.midas.vaadin.ui.common.HeaderActionBar;
import de.nihas101.midas.vaadin.ui.common.MidasView;
import de.nihas101.midas.vaadin.ui.common.QueryParameter;
import de.nihas101.midas.vaadin.ui.common.ShareholderPicker;
import de.nihas101.midas.vaadin.ui.common.YearPicker;
import de.nihas101.midas.vaadin.ui.common.locale.MidasLocaleResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;

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
    private final InterestRowService interestRowService;
    private final LockWriter lockWriter;
    private final ShareholderLock shareholderLock;
    private final ExportFactory exportFactory;
    private final BookingFactory bookingFactory;
    private final InterestCalculationFactory interestCalculationFactory;

    private BigDecimalField interestRateField;
    private HorizontalLayout actionRow;
    private Grid<InterestCalculationRow> interestCalculationGrid;
    private Checkbox updateInterestAutomaticallyToggle;
    private HeaderActionBar headerActionBar;
    private final DownloadTrigger downloadTrigger;

    public InterestView(
            final ShareholdersService shareholdersService,
            final InterestBookingsService bookingsService,
            final InterestRateService interestRateService,
            final CoreConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver,
            final InterestRowService interestRowService,
            final LockService lockWriter,
            final ShareholderLock shareholderLock,
            final ExportFactory exportFactory,
            final UserConfigFactory userConfigFactory,
            final BookingFactory bookingFactory,
            final InterestCalculationFactory interestCalculationFactory
    ) {
        super(
                config,
                userConfigService,
                messageSource,
                midasLocaleResolver,
                userConfigFactory
        );
        this.shareholdersService = shareholdersService;
        this.bookingsWriter = bookingsService;
        this.bookingsReader = bookingsService;
        this.interestRateService = interestRateService;
        this.messageSource = messageSource;
        this.interestRowService = interestRowService;
        this.lockWriter = lockWriter;
        this.shareholderLock = shareholderLock;
        this.exportFactory = exportFactory;
        this.bookingFactory = bookingFactory;
        this.interestCalculationFactory = interestCalculationFactory;

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();

        content.add(new H2(messageSource.getMessage("interest-calculation", null, getLocale())));

        this.downloadTrigger = new DownloadTrigger(content);

        setupHeader(content);
        setupInterestGrid(content);

        setContent(content);
    }


    // TODO: Also add these to local storage
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

        actionRow = createHeaderActionRow(locale);

        final Class<InterestView> viewClass = InterestView.class;
        final Runnable onUpdate = this::recalculateInterestForInitialDisplay;
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
                Set.of(ExportViewName.INTEREST)
        );

        content.add(headerActionBar);
    }

    private HorizontalLayout createHeaderActionRow(final Locale locale) {
        interestRateField = new BigDecimalField(messageSource.getMessage("interest.rate.label", null, locale));
        interestRateField.setMaxWidth("5em");
        interestRateField.setLocale(locale);
        interestRateField.setSuffixComponent(new Span("%"));
        interestRateField.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                recalculateInterest();
            }
        });

        updateInterestAutomaticallyToggle = new Checkbox(messageSource.getMessage("interest.update.automatically.toggle.label", null, locale));
        updateInterestAutomaticallyToggle.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                recalculateInterest();
            }
        });

        final HorizontalLayout actions = new HorizontalLayout();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.setAlignItems(FlexComponent.Alignment.BASELINE);
        actions.add(updateInterestAutomaticallyToggle, interestRateField);
        actions.setWidthFull();

        return actions;
    }

    // TODO: This and recalculateInterestForDisplay duplicate a lot of code and logic
    private void recalculateInterest() {
        final Shareholder shareholder = headerActionBar.getSelectedShareholder();
        final Year year = headerActionBar.getSelectedYear();
        final BigDecimal rate = interestRateField.getValue();
        if (shareholder == null || year == null) {
            return;
        }

        // TODO: Updating of these two fields should be handled in a transaction
        final InterestRate interestRate = updateInterestRate(shareholder, year, rate);
        final Bookings bookings = bookingsReader.interestRelatedBookingsForShareholderAndYear(shareholder.getId(), year);
        final InterestCalculation interestCalculation = interestCalculationFactory.create(
                bookings,
                year,
                interestRate.getInterestRate()
        );

        if (Boolean.TRUE.equals(updateInterestAutomaticallyToggle.getValue())) {
            updateInterestBooking(shareholder, year, interestCalculation);
        }

        interestCalculationGrid.setItems(
                interestRowService.generateRows(
                        year,
                        bookings,
                        rate,
                        interestCalculation,
                        getLocale()
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
        // TODO: Move this logic into the service. On interest update -> trigger
        if (booking != null) {
            // TODO: This mutates the object! Handle this differently
            booking.setAmount(interestCalculation.interest());
            bookingsWriter.update(booking);
        } else {
            final Booking newBooking = bookingFactory.create(
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

    private InterestRate updateInterestRate(
            final Shareholder shareholder,
            final Year year,
            final BigDecimal rate
    ) {
        InterestRate interestRate = interestRateService.interestRate(shareholder.getId(), year);
        if (interestRate != null) {
            interestRate.setInterestRate(rate); // TODO: This mutates the object! Do it differently
            interestRateService.update(interestRate);
        } else {
            interestRate = new InterestRate(null, shareholder.getId(), rate, year);
            interestRateService.create(interestRate);
        }
        return interestRate;
    }

    private void recalculateInterestForInitialDisplay() {
        final Shareholder shareholder = headerActionBar.getSelectedShareholder();
        final Year year = headerActionBar.getSelectedYear();

        final boolean hasSelection = shareholder != null && year != null;
        headerActionBar.setActionButtonsVisible(hasSelection);

        if (!hasSelection) {
            interestRateField.setValue(null);
            interestCalculationGrid.setItems(new ArrayList<>());
            return;
        }

        final boolean isLocked = shareholderLock.isLocked(shareholder, year);

        applyLockState(isLocked);

        final BigDecimal interestRate = interestRate(shareholder, year).getInterestRate();
        interestRateField.setValue(interestRate);

        // TODO: An exists check is enough
        final Booking booking = bookingsReader.systemGeneratedInterestForShareholderAndYear(shareholder, year);
        updateInterestAutomaticallyToggle.setValue(booking != null);

        final Bookings bookings = bookingsReader.interestRelatedBookingsForShareholderAndYear(shareholder.getId(), year);
        if (bookings.isEmpty()) {
            interestCalculationGrid.setItems(emptyList());
            return;
        }
        final InterestCalculation interestCalculation = new DefaultInterestCalculation(
                bookings,
                year,
                interestRate
        );
        interestCalculationGrid.setItems(
                interestRowService.generateRows(
                        year,
                        bookings,
                        interestRate,
                        interestCalculation,
                        getLocale()
                )
        );
    }

    private void applyLockState(final boolean isLocked) {
        if (isLocked) {
            headerActionBar.lockLockUnlockButton();
        } else {
            headerActionBar.unlockLockUnlockButton();
        }

        // Disable inputs when locked
        interestRateField.setReadOnly(isLocked);
        updateInterestAutomaticallyToggle.setEnabled(!isLocked);
    }

    private InterestRate interestRate(final Shareholder shareholder, final Year year) {
        InterestRate rate = interestRateService.interestRate(shareholder.getId(), year);
        if (rate == null) {
            return new InterestRate(null, shareholder.getId(), ZERO, year);
        }
        return rate;
    }

    private void setupInterestGrid(final VerticalLayout content) {
        interestCalculationGrid = new Grid<>();
        interestCalculationGrid.setEmptyStateText(messageSource.getMessage("bookings.table.empty-state-text", null, getLocale()));
        interestCalculationGrid.setWidthFull();
        interestCalculationGrid.setPartNameGenerator(InterestCalculationRow::partName);
        interestCalculationGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_COMPACT);

        setupColumn(interestCalculationGrid.addColumn(InterestCalculationRow::label), "interest.table.month", ColumnTextAlign.START);
        setupColumn(
                interestCalculationGrid.addColumn(
                        i -> Optional.ofNullable(i)
                                .map(InterestCalculationRow::totalTransaction)
                                .map(Transaction::moneyAmount)
                                .filter(t -> 0L != t.getCents())
                                .map(m -> m.format(getLocale()))
                                .orElse("")
                ), "interest.table.transactions", ColumnTextAlign.END);
        setupColumn(
                interestCalculationGrid.addColumn(
                        i -> Optional.ofNullable(i)
                                .map(InterestCalculationRow::totalTransaction)
                                .filter(t -> 0L != t.moneyAmount().getCents())
                                .map(Transaction::type)
                                .map(TransactionType::getValue)
                                .orElse("")
                ), "interest.table.sh", ColumnTextAlign.START);
        setupColumn(interestCalculationGrid.addColumn(
                i -> formatAmount(
                        Optional.ofNullable(i)
                                .map(InterestCalculationRow::balanceAtEndOfMonth)
                                .map(Transaction::moneyAmount)
                                .orElse(null)
                )
        ), "interest.table.balance", ColumnTextAlign.END);
        setupColumn(interestCalculationGrid.addColumn(
                i -> Optional.ofNullable(i)
                        .map(InterestCalculationRow::balanceAtEndOfMonth)
                        .map(Transaction::type)
                        .map(TransactionType::getValue)
                        .orElse("")
        ), "interest.table.sh", ColumnTextAlign.START);
        setupColumn(interestCalculationGrid.addColumn(
                i -> formatDays(
                        Optional.ofNullable(i)
                                .map(InterestCalculationRow::interestDaysCount)
                                .orElse(null)
                )
        ), "interest.table.days", ColumnTextAlign.CENTER);
        // TODO: We need to format the comma for the divisor in the summary only here -> Create a wrapper for this that handles this and only wrap the divisor
        setupColumn(interestCalculationGrid.addColumn(
                i -> formatInterestAmounts(
                        Optional.ofNullable(i)
                                .map(InterestCalculationRow::interestAmount)
                                .filter(t -> 0L != t.longValue())
                                .orElse(null)
                )
        ), "interest.table.interest-amount", ColumnTextAlign.CENTER);

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

    public static Icon icon() {
        return icon.create();
    }
}
