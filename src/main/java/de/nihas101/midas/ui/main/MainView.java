package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.example.service.NotifyingNumberWriter;
import de.nihas101.midas.example.service.NumberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

// TODO: Fancy drawer etc
@Slf4j
@Route("main")
@RouteAlias("")
@PageTitle("Main")
public class MainView extends VerticalLayout {

    public MainView(
            NumberService numberService,
            MidasConfig config,
            MessageSource messageSource,
            I18NProvider i18NProvider
    ) {
        addClassName("main-view");
        setSizeFull();
        setAlignItems(Alignment.START);
        setJustifyContentMode(JustifyContentMode.START);

        Locale locale = resolveLocale(config);
        log.debug("Locale of user: {}", locale);
        final MainHeader mainHeader = new MainHeader(
                config,
                messageSource,
                new LocaleSelect(
                        i18NProvider,
                        locale,
                        config
                ),
                locale
        );

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

        add(
                mainHeader,
                addNumberLayout,
                numbersTable,
                sumDisplay
        );
    }

    // TODO: Move to own class
    private static Locale resolveLocale(final MidasConfig config) {
        if (config.getI18n().isForceDefaultLanguage()) {
            return Locale.of(config.getI18n().getDefaultLocale());
        }
        // TODO: Also consider local storage
        Locale locale = UI.getCurrent().getLocale();
        return locale != null ? locale : Locale.of(config.getI18n().getDefaultLocale());
    }
}
