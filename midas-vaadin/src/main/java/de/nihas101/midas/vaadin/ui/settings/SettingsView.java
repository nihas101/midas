package de.nihas101.midas.vaadin.ui.settings;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
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

import java.util.ArrayList;
import java.util.List;

@Route("settings")
@PageTitle("Settings")
public class SettingsView extends MidasView {

    public static final VaadinIcon icon = VaadinIcon.COG;

    public record SettingRow(String title, String description, Component component) {
    }

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

        final Grid<SettingRow> settingsTable = settingsTable(config, i18NProvider, userConfigService, messageSource);
        content.add(settingsTable);
        content.setAlignSelf(FlexComponent.Alignment.CENTER, settingsTable);

        setContent(content);
    }

    private Grid<SettingRow> settingsTable(
            final CoreConfig config,
            final I18NProvider i18NProvider,
            final UserConfigService userConfigService,
            final MessageSource messageSource
    ) {
        final List<SettingRow> rows = new ArrayList<>();

        if (!config.getUi().isHideThemeToggle()) {
            final ThemeToggleButton themeToggleButton = new ThemeToggleButton(
                    config,
                    userConfigService
            );
            rows.add(new SettingRow(
                    messageSource.getMessage("settings.theme.title", null, getLocale()),
                    messageSource.getMessage("settings.theme.description", null, getLocale()),
                    themeToggleButton
            ));
        }

        if (!config.getUi().isHideLanguageSelector() && !config.getI18n().isForceDefaultLanguage()) {
            final LocaleSelect localeSelect = new LocaleSelect(
                    i18NProvider,
                    getLocale(),
                    config,
                    userConfigService
            );
            rows.add(new SettingRow(
                    messageSource.getMessage("settings.language.title", null, getLocale()),
                    messageSource.getMessage("settings.language.description", null, getLocale()),
                    localeSelect
            ));
        }

        final Grid<SettingRow> grid = new Grid<>();
        grid.setWidth("650px");
        grid.setAllRowsVisible(true);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addComponentColumn(row -> {
                    final Div container = new Div();
                    final Span titleSpan = new Span(row.title());
                    titleSpan.getStyle().set("font-weight", "600");
                    titleSpan.getStyle().set("display", "block");

                    final Span descSpan = new Span(row.description());
                    descSpan.getStyle().set("color", "var(--lumo-secondary-text-color)");
                    descSpan.getStyle().set("font-size", "var(--lumo-font-size-s)");
                    descSpan.getStyle().set("display", "block");

                    container.add(titleSpan, descSpan);
                    return container;
                }).setHeader(messageSource.getMessage("settings.table.description", null, getLocale()))
                .setFlexGrow(2)
                .setAutoWidth(true);

        grid.addComponentColumn(SettingRow::component)
                .setHeader(messageSource.getMessage("settings.table.setting", null, getLocale()))
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(1)
                .setAutoWidth(true);

        grid.setItems(rows);
        return grid;
    }

    public static Icon icon() {
        return icon.create();
    }
}

