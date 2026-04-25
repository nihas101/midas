package de.nihas101.midas.ui.settings;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.ui.common.MidasView;
import de.nihas101.midas.ui.common.locale.MidasLocaleResolver;
import de.nihas101.midas.userconfig.service.UserConfigService;
import org.springframework.context.MessageSource;

// TODO: Add descriptions to the settings
// TODO: Add property to hide settings
@Route("settings")
@PageTitle("Settings")
public class SettingsView extends MidasView {

    public static final VaadinIcon icon = VaadinIcon.COG;

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
        final VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);
        content.setAlignItems(FlexComponent.Alignment.START);

        content.add(new H2(messageSource.getMessage("settings", null, getLocale())));

        final VerticalLayout formContainer = new VerticalLayout();
        formContainer.setWidth("550px"); // Consistent width with other views
        formContainer.setPadding(false);
        formContainer.setSpacing(true);
        formContainer.setAlignItems(FlexComponent.Alignment.START);

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

        formContainer.add(themeToggleButton, localeSelect);
        content.add(formContainer);
        content.setAlignSelf(FlexComponent.Alignment.CENTER, formContainer);

        setContent(content);
    }

    public static Icon icon() {
        return icon.create();
    }
}
