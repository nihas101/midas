package de.nihas101.midas.ui.settings;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.userconfig.service.UserConfigService;


public class ThemeToggleButton extends Button {

    public ThemeToggleButton(
            final MidasConfig config,
            final UserConfigService userConfigService
    ) {
        this.setVisible(!config.getUi().isHideThemeToggle());
        this.setId("theme-toggle-button");

        this.addClickListener(event ->
                userConfigService.findByUserIdentifier(UserConfigService.DEFAULT_USER).ifPresent(userConfig -> {
                    String currentTheme = userConfig.getTheme() != null ? userConfig.getTheme() : "";
                    String newTheme = currentTheme.equals("dark") ? "" : "dark";
                    userConfig.setTheme(newTheme);
                    userConfigService.save(userConfig);
                    setTheme(newTheme);
                })
        );

        userConfigService.findByUserIdentifier(UserConfigService.DEFAULT_USER)
                .ifPresent(userConfig -> setThemeIcon(userConfig.getTheme()));
    }

    private void setTheme(final String theme) {
        UI.getCurrent().getPage().executeJs("document.documentElement.setAttribute('theme', $0);", theme);
        setThemeIcon(theme);
    }

    private void setThemeIcon(final String theme) {
        String icon = "dark".equals(theme) ? "☀️" : "🌙";
        this.setText(icon);
    }
}
