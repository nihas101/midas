package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.ui.accountstatement.AccountStatementView;
import de.nihas101.midas.ui.bookings.BookingsView;
import de.nihas101.midas.ui.common.MidasView;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.ui.interest.InterestView;
import de.nihas101.midas.ui.shareholders.ShareholdersView;
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
public class MainView extends MidasView {

    public static final VaadinIcon icon = VaadinIcon.HOME;

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

        final Div hubGrid = new Div();
        hubGrid.addClassName("hub-grid");
        hubGrid.setWidthFull();

        hubGrid.add(
                createHubCard(
                        ShareholdersView.icon(),
                        messageSource.getMessage("main.hub.shareholders.title", null, getLocale()),
                        messageSource.getMessage("main.hub.shareholders.description", null, getLocale()),
                        ShareholdersView.class
                )
        );

        hubGrid.add(
                createHubCard(
                        BookingsView.icon(),
                        messageSource.getMessage("main.hub.bookings.title", null, getLocale()),
                        messageSource.getMessage("main.hub.bookings.description", null, getLocale()),
                        BookingsView.class
                )
        );

        hubGrid.add(
                createHubCard(
                        AccountStatementView.icon(),
                        messageSource.getMessage("main.hub.account-statements.title", null, getLocale()),
                        messageSource.getMessage("main.hub.account-statements.description", null, getLocale()),
                        AccountStatementView.class
                )
        );

        hubGrid.add(
                createHubCard(
                        InterestView.icon(),
                        messageSource.getMessage("main.hub.interest.title", null, getLocale()),
                        messageSource.getMessage("main.hub.interest.description", null, getLocale()),
                        InterestView.class
                )
        );

        /*
        hubGrid.add(
                createHubCard(
                        ExportView.icon(),
                        messageSource.getMessage("main.hub.export.title", null, getLocale()),
                        messageSource.getMessage("main.hub.export.description", null, getLocale()),
                        ExportView.class
                )
        );
        */

        /*
        hubGrid.add(
                createHubCard(
                        BackupView.icon(),
                        messageSource.getMessage("main.hub.backup.title", null, getLocale()),
                        messageSource.getMessage("main.hub.backup.description", null, getLocale()),
                        BackupView.class
                )
        );
        */

        final VerticalLayout contentLayout = new VerticalLayout(hubGrid);
        contentLayout.setSizeFull();
        contentLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        contentLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        setContent(contentLayout);
    }

    private Div createHubCard(
            final Icon icon,
            final String title,
            final String description,
            final Class<? extends Component> navigationTarget
    ) {
        final Div card = new Div();
        card.addClassName("hub-card");

        final H2 h2 = new H2(title);
        final Paragraph p = new Paragraph(description);

        card.add(icon, h2, p);
        card.addClickListener(e -> card.getUI().ifPresent(ui -> ui.navigate(navigationTarget)));

        return card;
    }

    public static Icon icon() {
        return icon.create();
    }

}
