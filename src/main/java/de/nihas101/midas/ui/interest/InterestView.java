package de.nihas101.midas.ui.interest;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.ui.common.MidasPage;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

import java.util.Locale;

@Slf4j
@Route("interest")
@PageTitle("Interest")
public class InterestView extends MidasPage {

    public InterestView(
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

        // TODO: Add useful content here for calculating the interest

        setContent(contentLayout);
    }

}
