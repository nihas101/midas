package de.nihas101.midas.ui.bookings;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.row.BookingRow;
import de.nihas101.midas.bookings.service.BookingsService;
import de.nihas101.midas.interest.service.InterestUpdatingOpeningBalanceService;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.AbstractKaribuTest;
import de.nihas101.midas.ui.common.ShareholderPicker;
import de.nihas101.midas.ui.common.YearPicker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

import static com.github.mvysny.kaributesting.v10.LocatorJ._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.LocatorJ._setValue;
import static com.github.mvysny.kaributesting.v10.pro.ConfirmDialogKt._fireConfirm;

public class BookingsViewIT extends AbstractKaribuTest {

    @Autowired
    private ShareholdersService shareholdersService;

    @Autowired
    private BookingsService bookingsService;

    @Autowired
    private InterestUpdatingOpeningBalanceService openingBalanceService;

    @Test
    void testBookingsWorkflow() {
        // 1. Prepopulate a shareholder in the DB
        final Shareholder sh = new Shareholder(null, 101, "Alice", "Smith");
        shareholdersService.create(sh);

        final Shareholder savedSh = shareholdersService.shareholders().toList().stream()
                .filter(s -> "Alice".equals(s.getFirstName()) && "Smith".equals(s.getLastName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Failed to find created shareholder"));

        // 2. Navigate to BookingsView
        UI.getCurrent().navigate(BookingsView.class);

        // 3. Select Shareholder and Year
        final ShareholderPicker shareholderPicker = _get(ShareholderPicker.class);
        _setValue(shareholderPicker, savedSh);

        final YearPicker yearPicker = _get(YearPicker.class);
        _setValue(yearPicker, 2026);

        // 4. Set opening balance
        final BigDecimalField openingBalanceField = _get(BigDecimalField.class);
        _setValue(openingBalanceField, new BigDecimal("500.00"));

        // Verify opening balance is saved in the DB
        final OpeningBalance balance = openingBalanceService.openingBalance(savedSh.getId(), Year.of(2026));
        Assertions.assertNotNull(balance, "Opening balance should be saved in DB");
        Assertions.assertEquals(0, new BigDecimal("500.00").compareTo(balance.getOpeningBalance().toBigDecimalForInput()));

        // 5. Open Booking dialog and add a booking
        final Button addBookingButton = _get(Button.class, spec -> spec.withText("Add Booking"));
        _click(addBookingButton);

        // Find elements in the dialog
        final DatePicker datePicker = _get(DatePicker.class, spec -> spec.withLabel("Date"));
        final ComboBox<BookingType> typePicker = _get(ComboBox.class, spec -> spec.withLabel("Type"));
        final TextField commentField = _get(TextField.class, spec -> spec.withLabel("Comment"));
        final BigDecimalField amountField = _get(BigDecimalField.class, spec -> spec.withLabel("Amount"));

        _setValue(datePicker, LocalDate.of(2026, 5, 10));
        _setValue(typePicker, BookingType.WITHDRAWAL);
        _setValue(commentField, "Test Withdrawal Booking");
        _setValue(amountField, new BigDecimal("150.00"));

        final Button saveButton = _get(Button.class, spec -> spec.withText("Save"));
        _click(saveButton);

        // 6. Verify booking was saved in the DB
        final Bookings bookings = bookingsService.bookingsForShareholderAndYear(savedSh.getId(), Year.of(2026));
        Assertions.assertFalse(bookings.isEmpty(), "Bookings list should not be empty");

        final Booking createdBooking = bookings.filter(b -> true).bookings().stream()
                .filter(b -> "Test Withdrawal Booking".equals(b.getComment()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected booking not found in DB"));

        Assertions.assertEquals(0, new BigDecimal("150.00").compareTo(createdBooking.getAmount().toBigDecimalForInput()));
        Assertions.assertEquals(BookingType.WITHDRAWAL, createdBooking.getType());

        // 7. Verify the grid displays the booking row
        final Grid<BookingRow> grid = _get(Grid.class);
        Assertions.assertTrue(GridKt._size(grid) > 0, "Grid should have rows");
    }

    @Test
    void testDoubleBookingWarningAndSave() {
        // 1. Prepopulate a shareholder in the DB
        final Shareholder sh = new Shareholder(null, 102, "Bob", "Jones");
        shareholdersService.create(sh);

        final Shareholder savedSh = shareholdersService.shareholders().toList().stream()
                .filter(s -> "Bob".equals(s.getFirstName()) && "Jones".equals(s.getLastName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Failed to find created shareholder"));

        // 2. Navigate to BookingsView
        UI.getCurrent().navigate(BookingsView.class);

        // 3. Select Shareholder and Year
        final ShareholderPicker shareholderPicker = _get(ShareholderPicker.class);
        _setValue(shareholderPicker, savedSh);

        final YearPicker yearPicker = _get(YearPicker.class);
        _setValue(yearPicker, 2026);

        // 4. Set opening balance
        final BigDecimalField openingBalanceField = _get(BigDecimalField.class);
        _setValue(openingBalanceField, new BigDecimal("100.00"));

        // 5. Open Booking dialog and add first booking
        final Button addBookingButton = _get(Button.class, spec -> spec.withText("Add Booking"));
        _click(addBookingButton);

        // Find elements in the dialog
        final DatePicker datePicker = _get(DatePicker.class, spec -> spec.withLabel("Date"));
        final ComboBox<BookingType> typePicker = _get(ComboBox.class, spec -> spec.withLabel("Type"));
        final TextField commentField = _get(TextField.class, spec -> spec.withLabel("Comment"));
        final BigDecimalField amountField = _get(BigDecimalField.class, spec -> spec.withLabel("Amount"));

        _setValue(datePicker, LocalDate.of(2026, 6, 12));
        _setValue(typePicker, BookingType.WITHDRAWAL);
        _setValue(commentField, "Duplicate Booking Text");
        _setValue(amountField, new BigDecimal("50.00"));

        final Button saveButton = _get(Button.class, spec -> spec.withText("Save"));
        _click(saveButton);

        // Verify the first booking is saved
        Bookings bookings = bookingsService.bookingsForShareholderAndYear(savedSh.getId(), Year.of(2026));
        Assertions.assertEquals(1, bookings.filter(b -> true).bookings().size());

        // 6. Open Booking dialog again to add the second identical booking
        _click(addBookingButton);

        final DatePicker datePicker2 = _get(DatePicker.class, spec -> spec.withLabel("Date"));
        final ComboBox<BookingType> typePicker2 = _get(ComboBox.class, spec -> spec.withLabel("Type"));
        final TextField commentField2 = _get(TextField.class, spec -> spec.withLabel("Comment"));
        final BigDecimalField amountField2 = _get(BigDecimalField.class, spec -> spec.withLabel("Amount"));

        _setValue(datePicker2, LocalDate.of(2026, 6, 12));
        _setValue(typePicker2, BookingType.WITHDRAWAL);
        _setValue(commentField2, "Duplicate Booking Text");
        _setValue(amountField2, new BigDecimal("50.00"));

        _click(saveButton);

        // 7. Verify warning ConfirmDialog is shown
        final ConfirmDialog warningDialog = _get(ConfirmDialog.class);
        Assertions.assertNotNull(warningDialog);

        // Confirm the warning
        _fireConfirm(warningDialog);

        // 8. Verify both bookings are now saved
        bookings = bookingsService.bookingsForShareholderAndYear(savedSh.getId(), Year.of(2026));
        Assertions.assertEquals(2, bookings.filter(b -> true).bookings().size());
    }
}
