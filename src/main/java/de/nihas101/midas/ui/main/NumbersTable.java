package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.grid.Grid;
import de.nihas101.midas.example.entity.NumberEntity;
import de.nihas101.midas.example.service.NumberReader;

public class NumbersTable extends Grid<NumberEntity> implements Dependant {

    private final NumberReader numberReader;

    public NumbersTable(final NumberReader numberReader) {
        super(NumberEntity.class);
        this.numberReader = numberReader;
        this.setColumns("id", "value");
        this.getColumns().forEach(col -> col.setAutoWidth(true));
        this.setAllRowsVisible(true);
        this.update();
    }

    @Override
    public void update() {
        this.setItems(numberReader.getAllNumbers());
    }
}
