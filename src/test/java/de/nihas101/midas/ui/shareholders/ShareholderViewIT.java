package de.nihas101.midas.ui.shareholders;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.AbstractKaribuTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

import static com.github.mvysny.kaributesting.v10.LocatorJ._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._find;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.LocatorJ._setValue;

public class ShareholderViewIT extends AbstractKaribuTest {

    public static final Random RANDOM = new Random();
    @Autowired
    private ShareholdersService shareholdersService;

    @Test
    void createShareholder() {
        // Navigate to the view
        UI.getCurrent().navigate(ShareholdersView.class);

        // Find the table
        final ShareholdersTable table = _get(ShareholdersTable.class);

        // Click the "Add Shareholder" button in the last row (dummy row)
        // The button is inside a HorizontalLayout in the "actions" column
        final int lastRowIndex = GridKt._size(table) - 1;
        HorizontalLayout actions = (HorizontalLayout) GridKt._getCellComponent(table, lastRowIndex, "actions");
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
}
