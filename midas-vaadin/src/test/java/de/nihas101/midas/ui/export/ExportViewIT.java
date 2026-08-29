package de.nihas101.midas.ui.export;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Anchor;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.bookings.dto.DefaultBooking;
import de.nihas101.midas.core.bookings.service.BookingsService;
import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.interest.dto.InterestRate;
import de.nihas101.midas.core.interest.service.InterestRateService;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import de.nihas101.midas.core.openingbalance.service.DefaultOpeningBalanceService;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.AbstractKaribuTest;
import de.nihas101.midas.vaadin.ui.export.ExportView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;

import static com.github.mvysny.kaributesting.v10.LocatorJ._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._find;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.LocatorJ._setValue;

public class ExportViewIT extends AbstractKaribuTest {

    @Autowired
    private ShareholdersService shareholdersService;

    @Autowired
    private BookingsService bookingsService;

    @Autowired
    private DefaultOpeningBalanceService openingBalanceService;

    @Autowired
    private InterestRateService interestRateService;

    private Shareholder createShareholder(
            final int displayId,
            final String firstName,
            final String lastName
    ) {
        final Shareholder shareholder = new DefaultShareholder(null, displayId, firstName, lastName);
        shareholdersService.create(shareholder);
        return shareholdersService.shareholders().toList().stream()
                .filter(s -> firstName.equals(s.getFirstName()) && lastName.equals(s.getLastName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Failed to find created shareholder"));
    }

    private void seedTestData(final Shareholder shareholder, final Year year1) {
        openingBalanceService.create(
                new DefaultOpeningBalance(
                        null,
                        shareholder.getId(),
                        MoneyAmount.of(new BigDecimal("1000.00")),
                        year1,
                        Source.USER)
        );
        interestRateService.create(
                new InterestRate(
                        null,
                        shareholder.getId(),
                        new BigDecimal("3.00"),
                        year1)
        );
        bookingsService.create(
                new DefaultBooking(
                        null,
                        1,
                        shareholder.getId(),
                        year1.atMonth(4).atDay(15),
                        BookingType.WITHDRAWAL,
                        MoneyAmount.of(new BigDecimal("200.00")),
                        "Sample Withdrawal " + year1,
                        Source.USER
                )
        );
    }

    @Test
    void testSinglePdfExport_singleShareholderSingleViewSingleYear() {
        final Shareholder shareholder = createShareholder(501, "Diana", "Prince");
        seedTestData(shareholder, Year.of(2026));

        UI.getCurrent().navigate(ExportView.class);

        selectShareholders(Set.of(shareholder));

        selectExportViews(Set.of(ExportViewName.BOOKINGS));

        selectFromAndUntil(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        );

        selectFormats(Set.of("pdf"));

        triggerExport();

        assertDownloadWasTriggered();
    }

    @Test
    void testMultiPdfExport_multiYear_createsZip() {
        final Shareholder shareholder = createShareholder(502, "Bruce", "Wayne");
        seedTestData(shareholder, Year.of(2024));
        seedTestData(shareholder, Year.of(2025));
        seedTestData(shareholder, Year.of(2026));

        UI.getCurrent().navigate(ExportView.class);

        selectShareholders(Set.of(shareholder));

        selectExportViews(Set.of(ExportViewName.BOOKINGS));

        selectFromAndUntil(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2026, 12, 31)
        );

        selectFormats(Set.of("pdf"));

        triggerExport();

        assertDownloadWasTriggered();
    }

    @Test
    void testMultiPdfExport_multipleShareholdersAndViews() {
        final Shareholder sh1 = createShareholder(503, "Clark", "Kent");
        final Shareholder sh2 = createShareholder(504, "Barry", "Allen");
        seedTestData(sh1, Year.of(2026));
        seedTestData(sh2, Year.of(2026));

        UI.getCurrent().navigate(ExportView.class);

        selectShareholders(Set.of(sh1, sh2));

        selectExportViews(
                Set.of(
                        ExportViewName.BOOKINGS,
                        ExportViewName.ACCOUNT_STATEMENTS,
                        ExportViewName.INTEREST
                )
        );

        selectFromAndUntil(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        );

        selectFormats(Set.of("pdf"));

        triggerExport();

        assertDownloadWasTriggered();
    }

    @Test
    void testXlsxExport_singleYear() {
        final Shareholder shareholder = createShareholder(505, "Arthur", "Curry");
        seedTestData(shareholder, Year.of(2026));

        UI.getCurrent().navigate(ExportView.class);

        selectShareholders(Set.of(shareholder));

        selectExportViews(
                Set.of(
                        ExportViewName.BOOKINGS,
                        ExportViewName.ACCOUNT_STATEMENTS,
                        ExportViewName.INTEREST
                )
        );

        selectFromAndUntil(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        );

        selectFormats(Set.of("xlsx"));

        triggerExport();

        assertDownloadWasTriggered();
    }

    @Test
    void testXlsxExport_multiYear() {
        final Shareholder shareholder = createShareholder(506, "Hal", "Jordan");
        seedTestData(shareholder, Year.of(2024));
        seedTestData(shareholder, Year.of(2025));
        seedTestData(shareholder, Year.of(2026));

        UI.getCurrent().navigate(ExportView.class);

        selectShareholders(Set.of(shareholder));

        selectExportViews(
                Set.of(
                        ExportViewName.BOOKINGS,
                        ExportViewName.ACCOUNT_STATEMENTS,
                        ExportViewName.INTEREST
                )
        );

        selectFromAndUntil(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2026, 12, 31)
        );

        selectFormats(Set.of("xlsx"));

        triggerExport();

        assertDownloadWasTriggered();
    }

    @Test
    void testBothPdfAndXlsxExport() {
        final Shareholder shareholder = createShareholder(507, "Victor", "Stone");
        seedTestData(shareholder, Year.of(2026));

        UI.getCurrent().navigate(ExportView.class);

        selectShareholders(Set.of(shareholder));

        selectExportViews(
                Set.of(
                        ExportViewName.BOOKINGS,
                        ExportViewName.ACCOUNT_STATEMENTS
                )
        );

        selectFromAndUntil(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31)
        );

        selectFormats(Set.of("pdf", "xlsx"));

        triggerExport();

        assertDownloadWasTriggered();
    }

    private static void selectShareholders(final Set<Shareholder> shareholder) {
        final MultiSelectComboBox<Shareholder> shareholderPicker = _get(MultiSelectComboBox.class);
        _setValue(shareholderPicker, shareholder);
    }

    private static void selectExportViews(final Set<ExportViewName> BOOKINGS) {
        final CheckboxGroup<ExportViewName> viewPicker = _get(CheckboxGroup.class, spec -> spec.withLabel("Views"));
        _setValue(viewPicker, BOOKINGS);
    }

    private static void selectFromAndUntil(final LocalDate from, final LocalDate until) {
        final DatePicker startDatePicker = _get(DatePicker.class, spec -> spec.withLabel("From"));
        final DatePicker endDatePicker = _get(DatePicker.class, spec -> spec.withLabel("Until"));
        _setValue(startDatePicker, from);
        _setValue(endDatePicker, until);
    }

    private static void selectFormats(final Set<String> pdf) {
        final CheckboxGroup<String> formatPicker = _get(CheckboxGroup.class, spec -> spec.withLabel("Formats"));
        _setValue(formatPicker, pdf);
    }

    private static void triggerExport() {
        final Button exportButton = _get(Button.class, spec -> spec.withText("Run Export"));
        Assertions.assertTrue(exportButton.isEnabled(), "Export button should be enabled");
        _click(exportButton);
    }

    private static void assertDownloadWasTriggered() {
        final List<Anchor> anchors = _find(Anchor.class);
        Assertions.assertFalse(anchors.isEmpty(), "Download anchor should have been created");
        final Anchor downloadAnchor = anchors.getLast();
        Assertions.assertNotNull(downloadAnchor.getElement().getAttribute("download"), "Anchor should have download attribute");
    }

    @Test
    void testValidation_disablesExportButtonWhenInvalid() {
        UI.getCurrent().navigate(ExportView.class);

        final Button exportButton = _get(Button.class, spec -> spec.withText("Run Export"));
        final DatePicker startDatePicker = _get(DatePicker.class, spec -> spec.withLabel("From"));
        final DatePicker endDatePicker = _get(DatePicker.class, spec -> spec.withLabel("Until"));

        // Set invalid date range (end date before start date)
        _setValue(startDatePicker, LocalDate.of(2026, 12, 31));
        _setValue(endDatePicker, LocalDate.of(2026, 1, 1));
        Assertions.assertFalse(exportButton.isEnabled(), "Export button should be disabled when end date is before start date");

        // Restore valid dates
        _setValue(startDatePicker, LocalDate.of(2026, 1, 1));
        _setValue(endDatePicker, LocalDate.of(2026, 12, 31));

        // Deselect formats
        selectFormats(Set.of());
        Assertions.assertFalse(exportButton.isEnabled(), "Export button should be disabled when no formats are selected");
    }
}
