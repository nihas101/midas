package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.ui.theme.ThemeToggleButton;
import de.nihas101.midas.userconfig.service.UserConfigService;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class MainHeader extends HorizontalLayout {

    public MainHeader(
            final MidasConfig config,
            final MessageSource messageSource,
            final Select<Locale> languageSelect,
            final Locale locale,
            final UserConfigService userConfigService
    ) {
        this(
                new H1(messageSource.getMessage("welcome.message", null, locale)),
                new ThemeToggleButton(config, userConfigService),
                languageSelect
        );
    }

    protected MainHeader(H1 headerTitle, Button themeToggleButton, Select<Locale> languageSelect) {
        super(headerTitle, languageSelect, themeToggleButton);
        this.setWidthFull();
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        this.setAlignItems(FlexComponent.Alignment.CENTER);
        this.setWidthFull();
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        this.setAlignItems(FlexComponent.Alignment.CENTER);
    }
}
