package de.nihas101.midas.vaadin.ui.common;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.QueryParameters;
import de.nihas101.midas.api.shareholder.Shareholder;

import java.util.function.Function;

public class QueryParameter<C extends Component, T> implements HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<T>, T>> {

    public static final String QUERY_PARAM_SHAREHOLDER = "shareholder";
    public static final String QUERY_PARAM_YEAR = "year";

    private final String queryParameter;
    private final Class<C> viewClass;
    private final Function<T, String> toParameter;
    private final Runnable onUpdate;
    private final SelectionStorage selectionStorage;

    public QueryParameter(
            final String queryParameter,
            final Class<C> viewClass,
            final Function<T, String> toParameter,
            final Runnable onUpdate,
            final SelectionStorage selectionStorage
    ) {
        this.queryParameter = queryParameter;
        this.viewClass = viewClass;
        this.toParameter = toParameter;
        this.onUpdate = onUpdate;
        this.selectionStorage = selectionStorage;
    }

    public static QueryParameter<?, Shareholder> shareholderParameter(
            final Class<? extends Component> viewClass,
            final Runnable onUpdate,
            final SelectionStorage selectionStorage
    ) {
        return new QueryParameter<>(
                QUERY_PARAM_SHAREHOLDER,
                viewClass,
                shareholder -> String.valueOf(shareholder.getId()),
                onUpdate,
                selectionStorage
        );
    }

    public static QueryParameter<?, Integer> yearParameter(
            final Class<? extends Component> viewClass,
            final Runnable onUpdate,
            final SelectionStorage selectionStorage
    ) {
        return new QueryParameter<>(
                QUERY_PARAM_YEAR,
                viewClass,
                year -> String.valueOf(String.valueOf(year)),
                onUpdate,
                selectionStorage
        );
    }

    @Override
    public void valueChanged(final AbstractField.ComponentValueChangeEvent<ComboBox<T>, T> event) {
        final T value = event.getValue();

        if (QUERY_PARAM_SHAREHOLDER.equals(queryParameter)) {
            if (value instanceof Shareholder shareholder) {
                selectionStorage.setStoredShareholderId(shareholder.getId());
            } else {
                selectionStorage.setStoredShareholderId(null);
            }
        } else if (QUERY_PARAM_YEAR.equals(queryParameter)) {
            if (value instanceof Integer year) {
                selectionStorage.setStoredYear(year);
            } else {
                selectionStorage.setStoredYear(null);
            }
        }

        QueryParameters queryParameters = UI.getCurrent().getActiveViewLocation().getQueryParameters();
        if (value != null) {
            queryParameters = queryParameters.merging(queryParameter, toParameter.apply(value));
        } else {
            queryParameters = queryParameters.excluding(queryParameter);
        }
        UI.getCurrent().navigate(this.viewClass, queryParameters);
        this.onUpdate.run();
    }
}
