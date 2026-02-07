package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.example.service.NotifyingNumberWriter;
import de.nihas101.midas.example.service.NumberService;
import de.nihas101.midas.ui.common.MidasPage;
import de.nihas101.midas.ui.common.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

// TODO: Add ability to set favicon
// TODO: Add ability to set icon on main page
@Slf4j
@Route("main")
@RouteAlias("")
@PageTitle("Main")
public class MainView extends MidasPage {

    public MainView(
            NumberService numberService,
            MidasConfig config,
            MessageSource messageSource,
            UserConfigService userConfigService,
            MidasLocaleResolver midasLocaleResolver
    ) {
        super(
                config,
                userConfigService,
                messageSource,
                midasLocaleResolver
        );

        Locale locale = midasLocaleResolver.resolve();

        log.debug("Locale of user: {}", locale);
        final SumDisplay sumDisplay = new SumDisplay(
                numberService,
                messageSource,
                locale
        );
        final IntegerField numberField = new NumberField(
                messageSource,
                locale
        );
        final NumbersTable numbersTable = new NumbersTable(
                numberService,
                messageSource,
                locale
        );
        final AddNumberLayout addNumberLayout = new AddNumberLayout(
                numberField,
                new AddNumberButton(
                        numberField,
                        new NotifyingNumberWriter(
                                numberService,
                                List.of(
                                        numbersTable,
                                        sumDisplay
                                )
                        ),
                        messageSource,
                        locale
                ),
                messageSource
        );

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.addClassName("main-view-content");
        contentLayout.setSizeFull();
        contentLayout.setAlignItems(FlexComponent.Alignment.START);
        contentLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        contentLayout.add(
                addNumberLayout,
                numbersTable,
                sumDisplay
        );

        setContent(contentLayout);
    }

}
