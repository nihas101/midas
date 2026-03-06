package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.common.MidasLocaleResolver;
import de.nihas101.midas.ui.common.MidasPage;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.util.Locale;

// TODO: Add ability to set favicon
// TODO: Add ability to set icon on main page
@Slf4j
@Route("main")
@RouteAlias("")
@PageTitle("Main")
public class MainView extends MidasPage {

    public MainView(
            final ShareholdersService shareholdersService, // TODO: Move the shareholder grid into its own view
            final MidasConfig config,
            final MessageSource messageSource,
            final UserConfigService userConfigService,
            final MidasLocaleResolver midasLocaleResolver
    ) {
        super(
                config,
                userConfigService,
                messageSource,
                midasLocaleResolver
        );

        final Locale locale = midasLocaleResolver.resolve();

        final VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.addClassName("main-view-content");
        contentLayout.setSizeFull();
        contentLayout.setAlignItems(FlexComponent.Alignment.START);
        contentLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        contentLayout.add(
                new ShareholdersTable(
                        shareholdersService,
                        shareholdersService,
                        messageSource,
                        locale
                )
        );

        setContent(contentLayout);
    }

}
