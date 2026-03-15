package de.nihas101.midas.ui.shareholders;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.common.MidasPage;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

@Slf4j
@Route("shareholders")
@PageTitle("Shareholders") // TODO: Is it possible to add translations for the page title?
public class ShareholdersView extends MidasPage {

    public ShareholdersView(
            final ShareholdersService shareholdersService,
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
        contentLayout.addClassName("shareholders-view-content");
        contentLayout.setSizeFull();
        contentLayout.setAlignItems(FlexComponent.Alignment.START);
        contentLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        contentLayout.add(
                new ShareholdersTable(
                        shareholdersService,
                        shareholdersService,
                        messageSource,
                        getLocale()
                )
        );

        setContent(contentLayout);
    }

}
