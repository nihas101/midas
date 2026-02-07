package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.html.Span;
import de.nihas101.midas.example.service.NumberReader;

public class SumDisplay extends Span implements Dependant {

    private final NumberReader numberReader;

    public SumDisplay(NumberReader numberReader) {
        this.numberReader = numberReader;
        update();
    }

    @Override
    public void update() {
        this.setText("Sum: " + numberReader.getSum());
    }

}
