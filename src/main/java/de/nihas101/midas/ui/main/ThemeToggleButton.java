package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import de.nihas101.midas.config.ThemeConfig;

public class ThemeToggleButton extends Button {

    public ThemeToggleButton(ThemeConfig themeConfig) {
        this.setVisible(!themeConfig.isHideThemeToggle());
        this.setId("theme-toggle-button");
        this.addClickListener(event -> getUI()
                .map(UI::getPage)
                .ifPresent(p -> p.executeJs(
                        """
                                const currentTheme = document.documentElement.getAttribute('theme');
                                const newTheme = currentTheme === 'dark' ? '' : 'dark';
                                document.documentElement.setAttribute('theme', newTheme);
                                localStorage.setItem('theme', newTheme);
                                const button = document.getElementById('theme-toggle-button');
                                if (button) {
                                    button.textContent = newTheme === 'dark' ? '☀️' : '🌙';
                                }
                                """
                )));

        UI.getCurrent().getPage().executeJs(
                """
                        const theme = localStorage.getItem('theme') ?? %s;
                        document.documentElement.setAttribute('theme', theme || '');
                        const button = document.getElementById('theme-toggle-button');
                        if (button) {
                            const currentTheme = document.documentElement.getAttribute('theme');
                            button.textContent = currentTheme === 'dark' ? '☀️' : '🌙';
                        }
                        """.formatted(themeConfig.getDefaultTheme())
        );
    }
}
