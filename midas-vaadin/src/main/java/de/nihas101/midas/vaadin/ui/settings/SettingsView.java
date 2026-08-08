package de.nihas101.midas.vaadin.ui.settings;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nihas101.midas.api.userconfig.UserConfigFactory;
import de.nihas101.midas.api.userconfig.UserConfigService;
import de.nihas101.midas.core.config.CoreConfig;
import de.nihas101.midas.vaadin.ui.common.MidasView;
import de.nihas101.midas.vaadin.ui.common.locale.MidasLocaleResolver;
import org.springframework.context.MessageSource;

// TODO: Add descriptions to the settings
@Route("settings")
@PageTitle("Settings")
public class SettingsView extends MidasView {

    public static final VaadinIcon icon = VaadinIcon.COG;

    public SettingsView(
            final CoreConfig config,
            final I18NProvider i18NProvider,
            final UserConfigService userConfigService,
            final MessageSource messageSource,
            final MidasLocaleResolver midasLocaleResolver,
            final UserConfigFactory userConfigFactory
    ) {
        super(
                config,
                userConfigService,
                messageSource,
                midasLocaleResolver,
                userConfigFactory
        );
        final VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(true);
        content.setAlignItems(FlexComponent.Alignment.START);

        content.add(new H2(messageSource.getMessage("settings", null, getLocale())));

        final VerticalLayout formContainer = formContainer(config, i18NProvider, userConfigService);
        content.add(formContainer);
        content.setAlignSelf(FlexComponent.Alignment.CENTER, formContainer);

        setContent(content);
    }

    private VerticalLayout formContainer(
            final CoreConfig config,
            final I18NProvider i18NProvider,
            final UserConfigService userConfigService
    ) {
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
        return formContainer;
    }

    public static Icon icon() {
        return icon.create();
    }
}
