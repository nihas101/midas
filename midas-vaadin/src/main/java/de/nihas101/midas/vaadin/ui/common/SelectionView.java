package de.nihas101.midas.vaadin.ui.common;

import com.vaadin.flow.router.BeforeEnterEvent;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Slf4j
public class SelectionView {

    private final ShareholdersService shareholdersService;
    private final SelectionStorage selectionStorage;

    public SelectionView(
            final ShareholdersService shareholdersService,
            final SelectionStorage selectionStorage
    ) {
        this.shareholdersService = shareholdersService;
        this.selectionStorage = selectionStorage;
    }

    public void prepopulateSelection(
            final BeforeEnterEvent event,
            final HeaderActionBar headerActionBar,
            final Runnable onSelectionRestored
    ) {
        final Optional<String> shareholderParam = event.getLocation()
                .getQueryParameters()
                .getSingleParameter(QueryParameter.QUERY_PARAM_SHAREHOLDER);
        final Optional<String> yearParam = event.getLocation()
                .getQueryParameters()
                .getSingleParameter(QueryParameter.QUERY_PARAM_YEAR);

        final boolean hasUrlParam = shareholderParam.isPresent() || yearParam.isPresent();
        if (hasUrlParam) {
            setFromUrl(headerActionBar, shareholderParam, yearParam);
        } else {
            setFromStorage(headerActionBar, onSelectionRestored);
        }
    }

    private void setFromUrl(
            final HeaderActionBar headerActionBar,
            final Optional<String> shareholderParam,
            final Optional<String> yearParam
    ) {
        shareholderParam.ifPresent(shareholderId -> {
            if (StringUtils.isBlank(shareholderId)) {
                return;
            }
            try {
                final int shId = Integer.parseInt(shareholderId);
                final Shareholder shareholder = shareholdersService.shareholder(shId);
                if (shareholder == null) {
                    log.warn("Unknown shareholderId: {}. Ignoring parameter.", shareholderId);
                    return;
                }
                headerActionBar.setSelectedShareholder(shareholder);
                selectionStorage.setStoredShareholderId(shareholder.getId());
            } catch (NumberFormatException e) {
                log.warn("Unparsable shareholderId in query parameter: {}. Ignoring parameter.", shareholderId);
            }
        });

        yearParam.ifPresent(year -> {
            if (StringUtils.isBlank(year)) {
                return;
            }
            try {
                final int parsedYear = Integer.parseInt(year);
                headerActionBar.setSelectedYear(parsedYear);
                selectionStorage.setStoredYear(parsedYear);
            } catch (NumberFormatException e) {
                log.warn("Unparsable year in query parameter: {}. Ignoring parameter.", year);
            }
        });
    }

    private void setFromStorage(
            final HeaderActionBar headerActionBar,
            final Runnable onSelectionRestored
    ) {
        selectionStorage.getStoredSelection(storedSelection -> {
            boolean updated = false;
            if (storedSelection.shareholderId() != null) {
                final Shareholder shareholder = shareholdersService.shareholder(storedSelection.shareholderId());
                if (shareholder != null) {
                    headerActionBar.setSelectedShareholder(shareholder);
                    updated = true;
                }
            }
            if (storedSelection.year() != null) {
                headerActionBar.setSelectedYear(storedSelection.year());
                updated = true;
            }
            if (updated && onSelectionRestored != null) {
                onSelectionRestored.run();
            }
        });
    }
}
