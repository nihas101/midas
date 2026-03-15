package de.nihas101.midas.ui.settings;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.ui.common.MidasPage;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import org.springframework.context.MessageSource;

// TODO: Add descriptions to the settings
// TODO: Add property to hide settings
@Route("settings")
@PageTitle("Settings")
public class SettingsView extends MidasPage {

    public SettingsView(
            final MidasConfig config,
            final I18NProvider i18NProvider,
            final UserConfigService userConfigService,
            final MessageSource messageSource,
            final MidasLocaleResolver midasLocaleResolver
    ) {
        super(
                config,
                userConfigService,
                messageSource,
                midasLocaleResolver
        );
        final VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.setSpacing(true);
        verticalLayout.setPadding(true);
        verticalLayout.setAlignItems(FlexComponent.Alignment.START);

        final ThemeToggleButton themeToggleButton = new ThemeToggleButton(
                config,
                userConfigService
        );

        final LocaleSelect localeSelect = new LocaleSelect(
                i18NProvider,
                getLocale(),
                config,
                userConfigService
        );

        verticalLayout.add(themeToggleButton, localeSelect);
        setContent(verticalLayout);
    }
}
