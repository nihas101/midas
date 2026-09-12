package de.nihas101.midas.vaadin.ui.common;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.emptyMap;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectionViewTest {

    @Mock
    private ShareholdersService shareholdersService;

    @Mock
    private HeaderActionBar headerActionBar;

    @Mock
    private BeforeEnterEvent beforeEnterEvent;

    @Mock
    private Location location;

    @Mock
    private Shareholder shareholder;

    @Mock
    private SelectionStorage selectionStorage;

    private SelectionView delegate;

    @BeforeEach
    void setUp() {
        delegate = new SelectionView(shareholdersService, selectionStorage);
    }

    @Test
    void handleBeforeEnter_withValidShareholderAndYearQueryParams() {
        final QueryParameters queryParameters = new QueryParameters(Map.of(
                QueryParameter.QUERY_PARAM_SHAREHOLDER, List.of("42"),
                QueryParameter.QUERY_PARAM_YEAR, List.of("2024")
        ));
        when(beforeEnterEvent.getLocation()).thenReturn(location);
        when(location.getQueryParameters()).thenReturn(queryParameters);
        when(shareholder.getId()).thenReturn(42);
        when(shareholdersService.shareholder(42)).thenReturn(shareholder);

        final Runnable onSelectionRestored = mock(Runnable.class);

        delegate.prepopulateSelection(beforeEnterEvent, headerActionBar, onSelectionRestored);

        verify(headerActionBar).setSelectedShareholder(shareholder);
        verify(selectionStorage).setStoredShareholderId(42);

        verify(headerActionBar).setSelectedYear(2024);
        verify(selectionStorage).setStoredYear(2024);

        verifyNoInteractions(onSelectionRestored);
    }

    @Test
    void handleBeforeEnter_withInvalidQueryParams() {
        final QueryParameters queryParameters = new QueryParameters(Map.of(
                QueryParameter.QUERY_PARAM_SHAREHOLDER, List.of("abc"),
                QueryParameter.QUERY_PARAM_YEAR, List.of("invalidYear")
        ));
        when(beforeEnterEvent.getLocation()).thenReturn(location);
        when(location.getQueryParameters()).thenReturn(queryParameters);

        final Runnable onSelectionRestored = mock(Runnable.class);

        delegate.prepopulateSelection(beforeEnterEvent, headerActionBar, onSelectionRestored);

        verify(headerActionBar, never()).setSelectedShareholder(any());
        verify(selectionStorage, never()).setStoredShareholderId(any());

        verify(headerActionBar, never()).setSelectedYear(any());
        verify(selectionStorage, never()).setStoredYear(any());
    }

    @Test
    void handleBeforeEnter_withNonExistentShareholder() {
        final QueryParameters queryParameters = new QueryParameters(Map.of(
                QueryParameter.QUERY_PARAM_SHAREHOLDER, List.of("43"),
                QueryParameter.QUERY_PARAM_YEAR, List.of("1996")
        ));
        when(beforeEnterEvent.getLocation()).thenReturn(location);
        when(location.getQueryParameters()).thenReturn(queryParameters);
        when(shareholdersService.shareholder(43)).thenReturn(null);

        final Runnable onSelectionRestored = mock(Runnable.class);

        delegate.prepopulateSelection(beforeEnterEvent, headerActionBar, onSelectionRestored);

        verify(headerActionBar, never()).setSelectedShareholder(any());
        verify(selectionStorage, never()).setStoredShareholderId(43);

        verify(headerActionBar).setSelectedYear(1996);
        verify(selectionStorage).setStoredYear(1996);

        verifyNoInteractions(onSelectionRestored);
    }

    @Test
    void handleBeforeEnter_withoutQueryParams_restoresFromLocalStorage() {
        final QueryParameters emptyQueryParameters = new QueryParameters(emptyMap());
        when(beforeEnterEvent.getLocation()).thenReturn(location);
        when(location.getQueryParameters()).thenReturn(emptyQueryParameters);

        when(shareholdersService.shareholder(10)).thenReturn(shareholder);

        Mockito.doAnswer(invocation -> {
            Consumer<SelectionStorage.StoredSelection> consumer = invocation.getArgument(0);
            consumer.accept(new SelectionStorage.StoredSelection(10, 2023));
            return null;
        }).when(selectionStorage).getStoredSelection(any());

        final Runnable onSelectionRestored = mock(Runnable.class);

        delegate.prepopulateSelection(beforeEnterEvent, headerActionBar, onSelectionRestored);

        verify(headerActionBar).setSelectedShareholder(shareholder);
        verify(headerActionBar).setSelectedYear(2023);
        verify(onSelectionRestored).run();
    }
}
