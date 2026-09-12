package de.nihas101.midas.vaadin.ui.common;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import de.nihas101.midas.api.shareholder.Shareholder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryParameterTest {

    @Mock
    private SelectionStorage selectionStorage;

    @Mock
    private Runnable onUpdate;

    @Mock
    private UI ui;

    @Mock
    private Shareholder shareholder;

    private MockedStatic<UI> mockedUi;

    @BeforeEach
    void setUp() {
        mockedUi = mockStatic(UI.class);
        mockedUi.when(UI::getCurrent).thenReturn(ui);
    }

    @AfterEach
    void tearDown() {
        mockedUi.close();
    }

    @Test
    void shareholderParameter_valueChanged_withValue() {
        when(shareholder.getId()).thenReturn(123);
        when(ui.getActiveViewLocation()).thenReturn(new Location("test", new QueryParameters(Map.of("other", List.of("param")))));

        final QueryParameter<?, Shareholder> queryParam = QueryParameter.shareholderParameter(
                Div.class,
                onUpdate,
                selectionStorage
        );

        final ComboBox<Shareholder> comboBox = new ComboBox<>();
        final AbstractField.ComponentValueChangeEvent<ComboBox<Shareholder>, Shareholder> event =
                new AbstractField.ComponentValueChangeEvent<>(comboBox, comboBox, null, true) {
                    @Override
                    public Shareholder getValue() {
                        return shareholder;
                    }
                };

        queryParam.valueChanged(event);

        verify(selectionStorage).setStoredShareholderId(123);

        final ArgumentCaptor<QueryParameters> captor = ArgumentCaptor.forClass(QueryParameters.class);
        verify(ui).navigate(eq(Div.class), captor.capture());
        final QueryParameters capturedParams = captor.getValue();
        final Map<String, List<String>> parameters = capturedParams.getParameters();
        assertEquals(List.of("123"), parameters.get(QueryParameter.QUERY_PARAM_SHAREHOLDER));
        assertEquals(List.of("param"), parameters.get("other"));

        verify(onUpdate).run();
    }

    @Test
    void shareholderParameter_valueChanged_withNullValue() {
        when(ui.getActiveViewLocation()).thenReturn(new Location("test", new QueryParameters(Map.of(
                QueryParameter.QUERY_PARAM_SHAREHOLDER, List.of("123"),
                "other", List.of("param")
        ))));

        final QueryParameter<?, Shareholder> queryParam = QueryParameter.shareholderParameter(
                Div.class,
                onUpdate,
                selectionStorage
        );

        final ComboBox<Shareholder> comboBox = new ComboBox<>();
        final AbstractField.ComponentValueChangeEvent<ComboBox<Shareholder>, Shareholder> event =
                new AbstractField.ComponentValueChangeEvent<>(comboBox, comboBox, shareholder, true) {
                    @Override
                    public Shareholder getValue() {
                        return null;
                    }
                };

        queryParam.valueChanged(event);

        verify(selectionStorage).setStoredShareholderId(null);

        final ArgumentCaptor<QueryParameters> captor = ArgumentCaptor.forClass(QueryParameters.class);
        verify(ui).navigate(eq(Div.class), captor.capture());
        final QueryParameters capturedParams = captor.getValue();
        final Map<String, List<String>> parameters = capturedParams.getParameters();
        assertFalse(parameters.containsKey(QueryParameter.QUERY_PARAM_SHAREHOLDER));
        assertEquals(List.of("param"), parameters.get("other"));

        verify(onUpdate).run();
    }

    @Test
    void yearParameter_valueChanged_withValue() {
        when(ui.getActiveViewLocation()).thenReturn(new Location("test", new QueryParameters(Map.of())));

        final QueryParameter<?, Integer> queryParam = QueryParameter.yearParameter(
                Div.class,
                onUpdate,
                selectionStorage
        );

        final ComboBox<Integer> comboBox = new ComboBox<>();
        final AbstractField.ComponentValueChangeEvent<ComboBox<Integer>, Integer> event =
                new AbstractField.ComponentValueChangeEvent<>(comboBox, comboBox, null, true) {
                    @Override
                    public Integer getValue() {
                        return 2025;
                    }
                };

        queryParam.valueChanged(event);

        verify(selectionStorage).setStoredYear(2025);

        final ArgumentCaptor<QueryParameters> captor = ArgumentCaptor.forClass(QueryParameters.class);
        verify(ui).navigate(eq(Div.class), captor.capture());
        final QueryParameters capturedParams = captor.getValue();
        assertEquals(List.of("2025"), capturedParams.getParameters().get(QueryParameter.QUERY_PARAM_YEAR));

        verify(onUpdate).run();
    }

    @Test
    void yearParameter_valueChanged_withNullValue() {
        when(ui.getActiveViewLocation()).thenReturn(new Location("test", new QueryParameters(Map.of(
                QueryParameter.QUERY_PARAM_YEAR, List.of("2024")
        ))));

        final QueryParameter<?, Integer> queryParam = QueryParameter.yearParameter(
                Div.class,
                onUpdate,
                selectionStorage
        );

        final ComboBox<Integer> comboBox = new ComboBox<>();
        final AbstractField.ComponentValueChangeEvent<ComboBox<Integer>, Integer> event =
                new AbstractField.ComponentValueChangeEvent<>(comboBox, comboBox, 2024, true) {
                    @Override
                    public Integer getValue() {
                        return null;
                    }
                };

        queryParam.valueChanged(event);

        verify(selectionStorage).setStoredYear(null);

        final ArgumentCaptor<QueryParameters> captor = ArgumentCaptor.forClass(QueryParameters.class);
        verify(ui).navigate(eq(Div.class), captor.capture());
        final QueryParameters capturedParams = captor.getValue();
        final Map<String, List<String>> parameters = capturedParams.getParameters();
        assertFalse(parameters.containsKey(QueryParameter.QUERY_PARAM_YEAR));

        verify(onUpdate).run();
    }
}
