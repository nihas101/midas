package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import de.nihas101.midas.config.MidasConfig;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.IntStream;

public class YearPicker extends ComboBox<Integer> {

    public YearPicker(
            final String label,
            final ValueChangeListener<ComponentValueChangeEvent<ComboBox<Integer>, Integer>> changeListener,
            final MidasConfig midasConfig
    ) {
        this(
                label,
                changeListener,
                Math.max(1, midasConfig.getCleanup().getCutoff().getYears())
        );
    }

    private YearPicker(
            final String label,
            final ValueChangeListener<ComponentValueChangeEvent<ComboBox<Integer>, Integer>> changeListener,
            final int maxRange
    ) {
        this(
                label,
                IntStream.rangeClosed(0, maxRange)
                        .map(i -> LocalDate.now(ZoneId.systemDefault()).getYear() - i)
                        .boxed()
                        .toList(), changeListener
        );
    }

    public YearPicker(
            final String label,
            final List<Integer> selectableYears,
            final ValueChangeListener<ComponentValueChangeEvent<ComboBox<Integer>, Integer>> changeListener
    ) {
        super(label, selectableYears);
        this.setValue(LocalDate.now().getYear());
        this.setWidth(6, Unit.EM);
        this.addValueChangeListener(changeListener);
    }
}
