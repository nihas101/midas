package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.example.service.NumberService;
import de.nihas101.midas.example.service.NotifyingNumberWriter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Route("main")
@RouteAlias("")
@PageTitle("Main")
public class MainView extends VerticalLayout {

    public MainView(
            NumberService numberService,
            MidasConfig config
    ) {
        addClassName("main-view");
        setSizeFull();
        setAlignItems(Alignment.START);
        setJustifyContentMode(JustifyContentMode.START);

        final SumDisplay sumDisplay = new SumDisplay(numberService);
        final NumbersTable numbersTable = new NumbersTable(numberService);
        final IntegerField numberField = new NumberField();
        add(
                new MainHeader(
                        config.getTheme()
                ),
                new AddNumberLayout(
                        numberField,
                        new AddNumberButton(
                                numberField,
                                new NotifyingNumberWriter(
                                        numberService,
                                        List.of(
                                                numbersTable,
                                                sumDisplay
                                        )
                                )
                        )
                ),
                numbersTable,
                sumDisplay
        );
    }

}
