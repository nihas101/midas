package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.ui.main.MainView;
import de.nihas101.midas.ui.settings.SettingsView;
import de.nihas101.midas.ui.shareholders.ShareholdersView;
import de.nihas101.midas.userconfig.entity.UserConfig;
import de.nihas101.midas.userconfig.service.UserConfigService;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Optional;

public class MidasPage extends AppLayout {

    public MidasPage(
            final MidasConfig config,
            final UserConfigService userConfigService,
            final MessageSource messageSource,
            final MidasLocaleResolver midasLocaleResolver
    ) {
        final UserConfig userConfig = getUserConfig(userConfigService);
        applyTheme(userConfig.getTheme(), config.getTheme().getDefaultTheme());
        final Locale locale = midasLocaleResolver.resolve();

        HorizontalLayout navbarContent = new HorizontalLayout();
        navbarContent.setWidthFull();
        navbarContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navbarContent.setAlignItems(FlexComponent.Alignment.CENTER);

        final H1 title = new H1("Midas");
        title.getStyle().set("margin-left", "var(--lumo-space-m)");

        final Navbar navbar = new Navbar();
        navbarContent.add(new DrawerToggle(), title, navbar);
        addToNavbar(navbarContent);

        final RouterLink mainViewLink = new RouterLink(messageSource.getMessage("main.view", null, locale), MainView.class);
        final RouterLink shareholdersViewLink = new RouterLink(messageSource.getMessage("shareholders", null, locale), ShareholdersView.class);
        final RouterLink bookingsViewLink = new RouterLink(messageSource.getMessage("bookings", null, locale), de.nihas101.midas.ui.bookings.BookingsView.class);
        final RouterLink settingsLink = new RouterLink(messageSource.getMessage("settings", null, locale), SettingsView.class);

        final VerticalLayout drawerContent = new VerticalLayout(mainViewLink, shareholdersViewLink, bookingsViewLink, settingsLink);
        addToDrawer(drawerContent);
    }

    private UserConfig getUserConfig(final UserConfigService userConfigService) {
        final Optional<UserConfig> userConfig = userConfigService.findByUserIdentifier(UserConfigService.DEFAULT_USER);
        if (userConfig.isEmpty()) {
            UserConfig newUserConfig = new UserConfig(UserConfigService.DEFAULT_USER);
            userConfigService.save(newUserConfig);
            return newUserConfig;
        } else {
            return userConfig.get();
        }
    }

    private void applyTheme(
            final String theme,
            final String defaultTheme
    ) {
        final String effectiveTheme = (theme != null && !theme.isBlank()) ? theme : defaultTheme;
        UI.getCurrent().getPage().executeJs(
                "document.documentElement.setAttribute('theme', $0);",
                effectiveTheme
        );
    }
}
