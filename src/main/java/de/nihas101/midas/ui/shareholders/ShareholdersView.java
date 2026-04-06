package de.nihas101.midas.ui.shareholders;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.shareholders.service.ShareholdersService;
import de.nihas101.midas.ui.common.MidasView;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;

@Slf4j
@Route("shareholders")
@PageTitle("Shareholders") // TODO: Is it possible to add translations for the page title?
public class ShareholdersView extends MidasView {

    private static final VaadinIcon icon = VaadinIcon.USERS;

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

        final VerticalLayout content = new VerticalLayout();
        content.addClassName("shareholders-view-content");
        content.setSizeFull();
        content.setAlignItems(FlexComponent.Alignment.START);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        content.add(new H2(messageSource.getMessage("shareholders", null, getLocale())));

        content.add(
                new ShareholdersTable(
                        shareholdersService,
                        shareholdersService,
                        messageSource,
                        getLocale()
                )
        );

        setContent(content);
    }

    public static Icon icon() {
        return icon.create();
    }

}
