package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.nihas101.midas.config.ThemeConfig;

public class MainHeader extends HorizontalLayout {

    public MainHeader(final ThemeConfig theme) {
        this(
                new H1("Number List and Sum"),
                new ThemeToggleButton(theme)
        );
    }

    protected MainHeader(H1 headerTitle, Button themeToggleButton) {
        super(headerTitle, themeToggleButton);
        this.setWidthFull();
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        this.setAlignItems(FlexComponent.Alignment.CENTER);
        this.setWidthFull();
        this.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        this.setAlignItems(FlexComponent.Alignment.CENTER);
    }
}
