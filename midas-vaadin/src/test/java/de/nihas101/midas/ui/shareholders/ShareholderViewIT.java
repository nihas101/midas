package de.nihas101.midas.ui.shareholders;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import de.nihas101.midas.api.DeleteMode;
import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.bookings.dto.DefaultBooking;
import de.nihas101.midas.core.bookings.service.BookingsService;
import de.nihas101.midas.core.config.CoreConfig;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.AbstractKaribuTest;
import de.nihas101.midas.vaadin.ui.common.DeleteButton;
import de.nihas101.midas.vaadin.ui.common.GridHelper;
import de.nihas101.midas.vaadin.ui.shareholders.ShareholdersTable;
import de.nihas101.midas.vaadin.ui.shareholders.ShareholdersView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static com.github.mvysny.kaributesting.v10.LocatorJ._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._find;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.LocatorJ._setValue;
import static com.github.mvysny.kaributesting.v10.pro.ConfirmDialogKt._fireConfirm;

public class ShareholderViewIT extends AbstractKaribuTest {

    public static final Random RANDOM = new Random();
    @Autowired
    private ShareholdersService shareholdersService;

    @Autowired
    private BookingsService bookingsService;

    @Autowired
    private CoreConfig coreConfig;

    @Test
    void createShareholder() {
        // Navigate to the view
        UI.getCurrent().navigate(ShareholdersView.class);

        // Find the table
        final ShareholdersTable table = _get(ShareholdersTable.class);

        // Click the "Add Shareholder" button in the last row (dummy row)
        // The button is inside a HorizontalLayout in the "actions" column
        final int lastRowIndex = GridKt._size(table) - 1;
        HorizontalLayout actions = (HorizontalLayout) GridKt._getCellComponent(table, lastRowIndex, GridHelper.ACTIONS_KEY);
        Button addButton = _get(actions, Button.class);
        _click(addButton);

        // The editor is now open. Find the fields within the editor.
        // There are two TextFields: firstName and lastName.
        final List<TextField> fields = _find(TextField.class);
        TextField firstNameField = fields.get(0);
        TextField lastNameField = fields.get(1);

        final String john = "John" + RANDOM.nextInt();
        final String doe = "Doe" + RANDOM.nextInt();
        _setValue(firstNameField, john);
        _setValue(lastNameField, doe);

        // Click Save (the button in the editor actions column)
        final Button saveButton = _get(Button.class, spec -> spec.withText("Save"));
        _click(saveButton);

        // Verify the shareholder was created in the database
        final List<Shareholder> shareholders = shareholdersService.shareholders().toList();
        final boolean found = shareholders.stream()
                .anyMatch(s -> john.equals(s.getFirstName()) && doe.equals(s.getLastName()));

        Assertions.assertTrue(found, "Shareholder " + john + " " + doe + " should have been created");
    }

    @Test
    void deleteShareholder_whenRestrictedAndHasBookings_disablesButtonWithTooltipAndBlocksDelete() {
        // Prepopulate shareholder
        final Shareholder sh = new DefaultShareholder(null, 501, "Safe", "User");
        shareholdersService.create(sh);
        final Shareholder savedSh = shareholdersService.shareholders().toList().stream()
                .filter(s -> "Safe".equals(s.getFirstName()) && "User".equals(s.getLastName()))
                .findFirst()
                .orElseThrow();

        // Create booking for shareholder
        final Booking booking = DefaultBooking.builder()
                .shareholderId(savedSh.getId())
                .date(LocalDate.of(2026, 3, 1))
                .type(BookingType.COMPENSATION)
                .amount(MoneyAmount.of(new BigDecimal("100.00")))
                .comment("Associated booking")
                .build();
        bookingsService.create(booking);

        coreConfig.getShareholder().setDeleteMode(DeleteMode.RESTRICT);
        try {
            UI.getCurrent().navigate(ShareholdersView.class);
            final ShareholdersTable table = _get(ShareholdersTable.class);

            // Find index of our shareholder in table
            final List<Shareholder> items = GridKt._findAll(table);
            int rowIndex = -1;
            for (int i = 0; i < items.size(); i++) {
                if (savedSh.getId().equals(items.get(i).getId())) {
                    rowIndex = i;
                    break;
                }
            }
            Assertions.assertNotEquals(-1, rowIndex);

            final HorizontalLayout actions = (HorizontalLayout) GridKt._getCellComponent(table, rowIndex, GridHelper.ACTIONS_KEY);
            // When cannotDelete, the button is wrapped in a Span that carries the tooltip.
            final Span deleteWrapper = _get(actions, Span.class);
            final DeleteButton deleteButton = _get(deleteWrapper, DeleteButton.class);

            // Button should be disabled
            Assertions.assertFalse(deleteButton.isEnabled(), "Delete button should be disabled when shareholder has bookings");

            // Tooltip on the wrapper should explain why deletion is blocked
            Assertions.assertEquals("This shareholder cannot be deleted because there are still bookings associated with this shareholder.", Tooltip.forComponent(deleteWrapper).getText());

            // Shareholder still exists in database
            Assertions.assertNotNull(shareholdersService.shareholder(savedSh.getId()), "Shareholder should not be deleted");
        } finally {
            coreConfig.getShareholder().setDeleteMode(DeleteMode.CASCADE);
        }
    }

    @Test
    void deleteShareholder_whenRestrictedAndNoBookings_opensConfirmDialogAndDeletes() {
        // Prepopulate shareholder with no bookings
        final Shareholder sh = new DefaultShareholder(null, 502, "NoBookings", "User");
        shareholdersService.create(sh);
        final Shareholder savedSh = shareholdersService.shareholders().toList().stream()
                .filter(s -> "NoBookings".equals(s.getFirstName()) && "User".equals(s.getLastName()))
                .findFirst()
                .orElseThrow();

        coreConfig.getShareholder().setDeleteMode(DeleteMode.RESTRICT);
        try {
            UI.getCurrent().navigate(ShareholdersView.class);
            final ShareholdersTable table = _get(ShareholdersTable.class);

            final List<Shareholder> items = GridKt._findAll(table);
            int rowIndex = -1;
            for (int i = 0; i < items.size(); i++) {
                if (savedSh.getId().equals(items.get(i).getId())) {
                    rowIndex = i;
                    break;
                }
            }
            Assertions.assertNotEquals(-1, rowIndex);

            final HorizontalLayout actions = (HorizontalLayout) GridKt._getCellComponent(table, rowIndex, GridHelper.ACTIONS_KEY);
            final DeleteButton deleteButton = _get(actions, DeleteButton.class);
            _click(deleteButton);

            // ConfirmDialog should be opened
            final ConfirmDialog dialog = _get(ConfirmDialog.class);
            Assertions.assertNotNull(dialog);

            _fireConfirm(dialog);

            // Shareholder is deleted from DB
            Assertions.assertNull(shareholdersService.shareholder(savedSh.getId()), "Shareholder should be deleted");
        } finally {
            coreConfig.getShareholder().setDeleteMode(DeleteMode.CASCADE);
        }
    }

    @Test
    void deleteShareholder_whenCascadingAndHasBookings_opensConfirmDialog() {
        // Prepopulate shareholder with bookings
        final Shareholder sh = new DefaultShareholder(null, 503, "Unsafe", "User");
        shareholdersService.create(sh);
        final Shareholder savedSh = shareholdersService.shareholders().toList().stream()
                .filter(s -> "Unsafe".equals(s.getFirstName()) && "User".equals(s.getLastName()))
                .findFirst()
                .orElseThrow();

        final Booking booking = DefaultBooking.builder()
                .shareholderId(savedSh.getId())
                .date(LocalDate.of(2026, 3, 1))
                .type(BookingType.COMPENSATION)
                .amount(MoneyAmount.of(new BigDecimal("100.00")))
                .comment("Associated booking")
                .build();
        bookingsService.create(booking);

        coreConfig.getShareholder().setDeleteMode(DeleteMode.CASCADE);
        UI.getCurrent().navigate(ShareholdersView.class);
        final ShareholdersTable table = _get(ShareholdersTable.class);

        final List<Shareholder> items = GridKt._findAll(table);
        int rowIndex = -1;
        for (int i = 0; i < items.size(); i++) {
            if (savedSh.getId().equals(items.get(i).getId())) {
                rowIndex = i;
                break;
            }
        }
        Assertions.assertNotEquals(-1, rowIndex);

        final HorizontalLayout actions = (HorizontalLayout) GridKt._getCellComponent(table, rowIndex, GridHelper.ACTIONS_KEY);
        final DeleteButton deleteButton = _get(actions, DeleteButton.class);
        _click(deleteButton);

        // ConfirmDialog should open directly
        final ConfirmDialog dialog = _get(ConfirmDialog.class);
        Assertions.assertNotNull(dialog);
    }
}
