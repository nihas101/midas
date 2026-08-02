package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import de.nihas101.midas.core.config.MidasConfig;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class YearPicker extends ComboBox<Integer> {

    public YearPicker(
            final MessageSource messageSource,
            final Locale locale,
            final QueryParameter<?, Integer> queryParameter,
            final MidasConfig midasConfig
    ) {
        this(
                messageSource.getMessage("bookings.year", null, locale),
                queryParameter,
                Math.max(1, midasConfig.getCleanup().getCutoff().getYears())
        );
    }

    private YearPicker(
            final String label,
            final QueryParameter<?, Integer> queryParameter,
            final MidasConfig midasConfig
    ) {
        this(
                label,
                queryParameter,
                Math.max(1, midasConfig.getCleanup().getCutoff().getYears())
        );
    }

    private YearPicker(
            final String label,
            final QueryParameter<?, Integer> queryParameter,
            final int maxRange
    ) {
        this(
                label,
                IntStream.rangeClosed(0, maxRange)
                        .map(i -> LocalDate.now(ZoneId.systemDefault()).getYear() - i)
                        .boxed()
                        .toList(), queryParameter
        );
    }

    public YearPicker(
            final String label,
            final List<Integer> selectableYears,
            final QueryParameter<?, Integer> queryParameter
    ) {
        super(label, selectableYears);
        this.setValue(LocalDate.now().getYear());
        this.setWidth(6, Unit.EM);
        this.addValueChangeListener(queryParameter);
    }
}
