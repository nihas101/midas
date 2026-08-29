package de.nihas101.midas.ui.accountstatement;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import de.nihas101.midas.api.accountstatement.AccountStatementRow;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.accountstatement.service.DefaultAccountStatementService;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverrideEntity;
import de.nihas101.midas.persistance.accountstatements.AccountStatementOverridesRepository;
import de.nihas101.midas.ui.AbstractKaribuTest;
import de.nihas101.midas.vaadin.ui.accountstatement.AccountStatementView;
import de.nihas101.midas.vaadin.ui.common.ShareholderPicker;
import de.nihas101.midas.vaadin.ui.common.YearPicker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._find;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.LocatorJ._setValue;

public class AccountStatementViewIT extends AbstractKaribuTest {

    @Autowired
    private ShareholdersService shareholdersService;

    @Autowired
    private DefaultAccountStatementService accountStatementService;

    @Autowired
    private AccountStatementOverridesRepository overridesRepository;

    @Test
    void testAccountStatementWorkflow() {
        // 1. Prepopulate a shareholder in the DB
        final Shareholder sh = new DefaultShareholder(null, 102, "Bob", "Jones");
        shareholdersService.create(sh);

        final Shareholder savedSh = shareholdersService.shareholders().toList().stream()
                .filter(s -> "Bob".equals(s.getFirstName()) && "Jones".equals(s.getLastName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Failed to find created shareholder"));

        // 2. Navigate to AccountStatementView
        UI.getCurrent().navigate(AccountStatementView.class);

        // 3. Select Shareholder and Year
        final ShareholderPicker shareholderPicker = _get(ShareholderPicker.class);
        _setValue(shareholderPicker, savedSh);

        final YearPicker yearPicker = _get(YearPicker.class);
        _setValue(yearPicker, 2026);

        // 4. Click the "Add entry" button to add a manual entry
        final Button addManualRowBtn = _get(Button.class, spec -> spec.withText("Add entry"));
        _click(addManualRowBtn);

        // Find elements in the ManualRowDialog
        final TextField labelField = _get(TextField.class, spec -> spec.withLabel("Type"));
        final BigDecimalField amountField = _get(BigDecimalField.class, spec -> spec.withLabel("Amount"));

        _setValue(labelField, "Bonus Dividend");
        _setValue(amountField, new BigDecimal("250.00"));

        final Button saveButton = _get(Button.class, spec -> spec.withText("Save"));
        _click(saveButton);

        // 5. Verify manual entry is saved in the database
        final List<AccountStatementOverrideEntity> overrides = overridesRepository.findByShareholderIdAndYear(savedSh.getId(), 2026);
        Assertions.assertFalse(overrides.isEmpty(), "Overrides should not be empty");

        final AccountStatementOverrideEntity bonusOverride = overrides.stream()
                .filter(o -> "Bonus Dividend".equals(o.getLabelOverride()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected manual override not found in DB"));

        Assertions.assertEquals(0, new BigDecimal("250.00").compareTo(bonusOverride.getAmount().toBigDecimalForInput()));

        // 6. Verify grids display items
        final List<Grid> grids = _find(Grid.class);
        Assertions.assertTrue(grids.size() >= 2, "Should have at least 2 grids (account statement and closing statement)");

        final Grid<AccountStatementRow> accountStatementGrid = (Grid<AccountStatementRow>) grids.get(0);
        Assertions.assertTrue(GridKt._size(accountStatementGrid) > 0, "Account statement grid should not be empty");
    }
}
