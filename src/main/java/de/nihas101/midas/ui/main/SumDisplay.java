package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.html.Span;
import de.nihas101.midas.example.service.NumberReader;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class SumDisplay extends Span implements Dependant {

    private final NumberReader numberReader;
    private final MessageSource messageSource;
    private final Locale locale;

    public SumDisplay(
            NumberReader numberReader,
            MessageSource messageSource,
            Locale locale
    ) {
        this.numberReader = numberReader;
        this.messageSource = messageSource;
        this.locale = locale;
        update();
    }

    @Override
    public void update() {
        this.setText(messageSource.getMessage("sum.display", new Object[]{numberReader.getSum()}, locale));
    }

}
