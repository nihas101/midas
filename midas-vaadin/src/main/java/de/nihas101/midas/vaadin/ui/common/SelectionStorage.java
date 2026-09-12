package de.nihas101.midas.vaadin.ui.common;

import com.vaadin.flow.component.UI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class SelectionStorage {

    public static final String KEY_SHAREHOLDER_ID = "midas.selectedShareholderId";
    public static final String KEY_YEAR = "midas.selectedYear";

    public record StoredSelection(Integer shareholderId, Integer year) {
    }

    public void setStoredShareholderId(final Integer shareholderId) {
        final UI ui = UI.getCurrent();
        if (ui == null) {
            return;
        }
        if (shareholderId != null) {
            ui.getPage().executeJs(
                    "window.localStorage.setItem($0, $1);",
                    KEY_SHAREHOLDER_ID,
                    String.valueOf(shareholderId)
            );
        } else {
            ui.getPage().executeJs(
                    "window.localStorage.removeItem($0);",
                    KEY_SHAREHOLDER_ID
            );
        }
    }

    public void setStoredYear(final Integer year) {
        final UI ui = UI.getCurrent();
        if (ui == null) {
            return;
        }
        if (year != null) {
            ui.getPage().executeJs(
                    "window.localStorage.setItem($0, $1);",
                    KEY_YEAR,
                    String.valueOf(year)
            );
        } else {
            ui.getPage().executeJs(
                    "window.localStorage.removeItem($0);",
                    KEY_YEAR
            );
        }
    }

    public void getStoredSelection(final Consumer<StoredSelection> callback) {
        final UI ui = UI.getCurrent();
        if (ui == null) {
            return;
        }
        ui.getPage().executeJs(
                "return (window.localStorage.getItem($0) || '') + '|||' + (window.localStorage.getItem($1) || '');",
                KEY_SHAREHOLDER_ID,
                KEY_YEAR
        ).then(String.class, result -> {
            final String[] parts = splitIntoParts(result);
            final Integer shareholderId = parseShareholderId(parts);
            final Integer year = parseYear(parts);
            callback.accept(new StoredSelection(shareholderId, year));
        });
    }

    private Integer parseShareholderId(final String[] parts) {
        if (parts.length == 0) {
            return null;
        }
        final String shId = parts[0];
        if (!shId.isBlank() && !"null".equalsIgnoreCase(shId)) {
            try {
                return Integer.parseInt(shId);
            } catch (NumberFormatException e) {
                log.warn("Unparsable stored shareholderId: {}", shId);
            }
        }
        return null;
    }

    private Integer parseYear(final String[] parts) {
        if (parts.length < 2) {
            return null;
        }
        final String y = parts[1];
        if (!y.isBlank() && !"null".equalsIgnoreCase(y)) {
            try {
                return Integer.parseInt(y);
            } catch (NumberFormatException e) {
                log.warn("Unparsable stored year: {}", y);
            }
        }
        return null;
    }

    private String[] splitIntoParts(final String result) {
        if (result != null && result.contains("|||")) {
            return result.split("\\|\\|\\|", -1);
        } else {
            return new String[0];
        }
    }
}
