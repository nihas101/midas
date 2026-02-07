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
        Optional<UserConfig> userConfig = userConfigService.findByUserIdentifier(UserConfigService.DEFAULT_USER);
        if (userConfig.isEmpty()) {
            UserConfig newUserConfig = new UserConfig(UserConfigService.DEFAULT_USER);
            userConfigService.save(newUserConfig);
            userConfig = Optional.of(newUserConfig);
        }
        applyTheme(userConfig.get().getTheme(), config.getTheme().getDefaultTheme());
        Locale locale = midasLocaleResolver.resolve();

        HorizontalLayout navbarContent = new HorizontalLayout();
        navbarContent.setWidthFull();
        navbarContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navbarContent.setAlignItems(FlexComponent.Alignment.CENTER);

        H1 title = new H1("Midas");
        title.getStyle().set("margin-left", "var(--lumo-space-m)");

        Navbar navbar = new Navbar();
        navbarContent.add(new DrawerToggle(), title, navbar);
        addToNavbar(navbarContent);

        RouterLink mainViewLink = new RouterLink(messageSource.getMessage("main.view", null, locale), MainView.class);
        RouterLink settingsLink = new RouterLink(messageSource.getMessage("settings", null, locale), SettingsView.class);

        VerticalLayout drawerContent = new VerticalLayout(mainViewLink, settingsLink);
        addToDrawer(drawerContent);
    }

    private void applyTheme(String theme, String defaultTheme) {
        String effectiveTheme = (theme != null && !theme.isBlank()) ? theme : defaultTheme;
        UI.getCurrent().getPage().executeJs(
                "document.documentElement.setAttribute('theme', $0);",
                effectiveTheme
        );
    }
}
