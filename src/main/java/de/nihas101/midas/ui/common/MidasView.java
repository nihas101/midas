package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.ui.accountstatement.AccountStatementView;
import de.nihas101.midas.ui.bookings.BookingsView;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.ui.interest.InterestView;
import de.nihas101.midas.ui.main.MainView;
import de.nihas101.midas.ui.settings.SettingsView;
import de.nihas101.midas.ui.shareholders.ShareholdersView;
import de.nihas101.midas.userconfig.entity.UserConfig;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Optional;

@Getter
public class MidasView extends AppLayout {

    public static final String QUERY_PARAM_SHAREHOLDER = "shareholder";
    public static final String QUERY_PARAM_YEAR = "year";

    private final Locale locale;

    public MidasView(
            final MidasConfig config,
            final UserConfigService userConfigService,
            final MessageSource messageSource,
            final MidasLocaleResolver midasLocaleResolver
    ) {
        final UserConfig userConfig = getUserConfig(userConfigService);
        applyTheme(userConfig.getTheme(), config.getTheme().getDefaultTheme());
        locale = midasLocaleResolver.resolve();

        HorizontalLayout navbarContent = new HorizontalLayout();
        navbarContent.setWidthFull();
        navbarContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navbarContent.setAlignItems(FlexComponent.Alignment.CENTER);

        final H1 title = new H1(config.getTitle());
        title.getStyle().set("margin-left", "var(--lumo-space-m)");

        final Navbar navbar = new Navbar();
        navbarContent.add(new DrawerToggle(), title, navbar);
        addToNavbar(navbarContent);

        final VerticalLayout drawerContent = new VerticalLayout(
                createMenuLink(MainView.icon(), messageSource.getMessage("main.view", null, locale), MainView.class),
                createMenuLink(ShareholdersView.icon(), messageSource.getMessage("shareholders", null, locale), ShareholdersView.class),
                createMenuLink(BookingsView.icon(), messageSource.getMessage("bookings", null, locale), BookingsView.class),
                createMenuLink(AccountStatementView.icon(), messageSource.getMessage("account-statements", null, locale), AccountStatementView.class),
                createMenuLink(InterestView.icon(), messageSource.getMessage("interest-calculation", null, locale), InterestView.class),
                //createMenuLink(ExportView.icon(), messageSource.getMessage("export", null, locale), ExportView.class),
                //createMenuLink(BackupView.icon(), messageSource.getMessage("backup", null, locale), BackupView.class),
                createMenuLink(SettingsView.icon(), messageSource.getMessage("settings", null, locale), SettingsView.class)
        );
        addToDrawer(drawerContent);
        setDrawerOpened(false);
    }

    private RouterLink createMenuLink(
            final Icon icon,
            final String label,
            final Class<? extends Component> target
    ) {
        icon.getStyle().set("margin-right", "var(--lumo-space-s)");
        icon.getStyle().set("padding", "var(--lumo-space-xs)");

        final RouterLink link = new RouterLink(target);
        link.add(icon, new Span(label));
        link.getStyle().set("display", "flex");
        link.getStyle().set("align-items", "center");
        link.getStyle().set("text-decoration", "none");
        link.getStyle().set("color", "inherit");
        return link;
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
