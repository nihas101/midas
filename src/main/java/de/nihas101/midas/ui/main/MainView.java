package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.example.service.NotifyingNumberWriter;
import de.nihas101.midas.example.service.NumberService;
import de.nihas101.midas.ui.locale.LocaleSelect;
import de.nihas101.midas.ui.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.entity.UserConfig;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

// TODO: Add ability to set favicon
// TODO: Add ability to set icon on main page
@Slf4j
@Route("main")
@RouteAlias("")
@PageTitle("Main")
public class MainView extends AppLayout {

    public MainView(
            NumberService numberService,
            MidasConfig config,
            MessageSource messageSource,
            I18NProvider i18NProvider,
            UserConfigService userConfigService,
            MidasLocaleResolver midasLocaleResolver
    ) {
        addToNavbar(new DrawerToggle());
        H1 title = new H1("Midas");
        title.getStyle().set("margin-left", "var(--lumo-space-m)");
        addToNavbar(title);


        Optional<UserConfig> userConfig = userConfigService.findByUserIdentifier(UserConfigService.DEFAULT_USER);
        if (userConfig.isEmpty()) {
            UserConfig newUserConfig = new UserConfig(UserConfigService.DEFAULT_USER);
            userConfigService.save(newUserConfig);
            userConfig = Optional.of(newUserConfig);
        }

        applyTheme(userConfig.get().getTheme(), config.getTheme().getDefaultTheme());

        Locale locale = midasLocaleResolver.resolve();

        log.debug("Locale of user: {}", locale);
        final MainHeader mainHeader = new MainHeader(
                config,
                messageSource,
                new LocaleSelect(
                        i18NProvider,
                        locale,
                        config,
                        userConfigService
                ),
                locale,
                userConfigService
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

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.addClassName("main-view-content");
        contentLayout.setSizeFull();
        contentLayout.setAlignItems(FlexComponent.Alignment.START);
        contentLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        contentLayout.add(
                mainHeader,
                addNumberLayout,
                numbersTable,
                sumDisplay
        );

        setContent(contentLayout);
    }

    private void applyTheme(String theme, String defaultTheme) {
        String effectiveTheme = (theme != null && !theme.isBlank()) ? theme : defaultTheme;
        UI.getCurrent().getPage().executeJs(
                "document.documentElement.setAttribute('theme', $0);",
                effectiveTheme
        );
    }
}
