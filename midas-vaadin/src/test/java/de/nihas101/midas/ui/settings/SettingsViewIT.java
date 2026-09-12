package de.nihas101.midas.ui.settings;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import de.nihas101.midas.ui.AbstractKaribuTest;
import de.nihas101.midas.vaadin.ui.settings.LocaleSelect;
import de.nihas101.midas.vaadin.ui.settings.SettingsView;
import de.nihas101.midas.vaadin.ui.settings.ThemeToggleButton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

public class SettingsViewIT extends AbstractKaribuTest {

    @Test
    void testSettingsViewRendersTableWithDescriptions() {
        UI.getCurrent().navigate(SettingsView.class);

        @SuppressWarnings("unchecked")
        final Grid<SettingsView.SettingRow> grid = _get(Grid.class);
        Assertions.assertNotNull(grid);

        final List<SettingsView.SettingRow> rows = GridKt._findAll(grid);
        Assertions.assertFalse(rows.isEmpty());

        // Verify Theme row exists and contains description and ThemeToggleButton
        final SettingsView.SettingRow themeRow = rows.stream()
                .filter(r -> r.component() instanceof ThemeToggleButton)
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(themeRow, "Theme toggle row should be present");
        Assertions.assertNotNull(themeRow.title());
        Assertions.assertNotNull(themeRow.description());

        // Verify Language row exists and contains description and LocaleSelect
        final SettingsView.SettingRow languageRow = rows.stream()
                .filter(r -> r.component() instanceof LocaleSelect)
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(languageRow, "Language select row should be present");
        Assertions.assertNotNull(languageRow.title());
        Assertions.assertNotNull(languageRow.description());

        // Verify buttons can be located via their SettingRow
        Assertions.assertTrue(themeRow.component() instanceof ThemeToggleButton);
        Assertions.assertTrue(languageRow.component() instanceof LocaleSelect);
    }
}
