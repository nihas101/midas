package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.ui.common.MidasPage;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

// TODO: Add ability to set favicon
// TODO: Add ability to set icon on main page
// TODO: Redirect here for 404s
@Slf4j
@Route("main")
@RouteAlias("")
@PageTitle("Main")
public class MainView extends MidasPage {

    public MainView(
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

        final VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.addClassName("main-view-content");
        contentLayout.setSizeFull();
        contentLayout.setAlignItems(FlexComponent.Alignment.START);
        contentLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        // TODO: Add useful content here
        // TODO: Add a section reminiscent of the original here

        setContent(contentLayout);
    }

}
