package de.nihas101.midas.ui.interest;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.BigDecimalField;
import de.nihas101.midas.core.bookings.dto.Booking;
import de.nihas101.midas.core.bookings.dto.Bookings;
import de.nihas101.midas.core.bookings.entity.BookingType;
import de.nihas101.midas.core.bookings.service.BookingsService;
import de.nihas101.midas.core.interest.dto.InterestRate;
import de.nihas101.midas.core.interest.row.InterestCalculationRow;
import de.nihas101.midas.core.interest.service.InterestRateService;
import de.nihas101.midas.core.shareholders.dto.Shareholder;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.AbstractKaribuTest;
import de.nihas101.midas.ui.common.ShareholderPicker;
import de.nihas101.midas.ui.common.YearPicker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Year;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.LocatorJ._setValue;

public class InterestViewIT extends AbstractKaribuTest {

    @Autowired
    private ShareholdersService shareholdersService;

    @Autowired
    private InterestRateService interestRateService;

    @Autowired
    private BookingsService bookingsService;

    @Test
    void testInterestCalculationWorkflow() {
        // 1. Prepopulate a shareholder in the DB
        final Shareholder sh = new Shareholder(null, 103, "Charlie", "Brown");
        shareholdersService.create(sh);

        final Shareholder savedSh = shareholdersService.shareholders().toList().stream()
                .filter(s -> "Charlie".equals(s.getFirstName()) && "Brown".equals(s.getLastName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Failed to find created shareholder"));

        // 2. Navigate to InterestView
        UI.getCurrent().navigate(InterestView.class);

        // 3. Select Shareholder and Year
        final ShareholderPicker shareholderPicker = _get(ShareholderPicker.class);
        _setValue(shareholderPicker, savedSh);

        final YearPicker yearPicker = _get(YearPicker.class);
        _setValue(yearPicker, 2026);

        // 4. Set interest rate
        final BigDecimalField interestRateField = _get(BigDecimalField.class);
        _setValue(interestRateField, new BigDecimal("2.50"));

        // Toggle auto-update interest
        final Checkbox updateAutoToggle = _get(Checkbox.class);
        _setValue(updateAutoToggle, true);

        // 5. Verify interest rate is saved in DB
        final InterestRate interestRate = interestRateService.interestRate(savedSh.getId(), Year.of(2026));
        Assertions.assertNotNull(interestRate, "Interest rate should be saved in DB");
        Assertions.assertEquals(0, new BigDecimal("2.50").compareTo(interestRate.getInterestRate()));

        // 6. Verify that an interest booking is generated/updated
        final Bookings bookings = bookingsService.bookingsForShareholderAndYear(savedSh.getId(), Year.of(2026));
        final Booking interestBooking = bookings.filter(b -> true).bookings().stream()
                .filter(b -> BookingType.INTEREST.equals(b.getType()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected interest booking not found in DB"));

        // 7. Verify the grid has rows
        @SuppressWarnings("unchecked") final Grid<InterestCalculationRow> grid = _get(Grid.class);
        Assertions.assertTrue(GridKt._size(grid) > 0, "Grid should have rows");
    }
}
