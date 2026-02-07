package de.nihas101.midas.ui.settings;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.i18n.I18NProvider;
import de.nihas101.midas.config.MidasConfig;
import de.nihas101.midas.userconfig.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

@Slf4j
public class LocaleSelect extends Select<Locale> {

    public LocaleSelect(
            I18NProvider i18NProvider,
            final Locale locale,
            MidasConfig config,
            UserConfigService userConfigService
    ) {
        final List<Locale> providedLocales = i18NProvider.getProvidedLocales();
        this.setVisible(isVisible(config));
        this.setItems(providedLocales);
        this.setItemLabelGenerator(loc -> loc.getDisplayName(loc));
        this.setValue(locale);
        this.addValueChangeListener(event -> {
            Locale selectedLocale = event.getValue();
            if (selectedLocale == null) {
                return;
            }

            userConfigService.findByUserIdentifier(UserConfigService.DEFAULT_USER)
                    .ifPresent(userConfig -> {
                        userConfig.setLocale(selectedLocale.toLanguageTag());
                        userConfigService.save(userConfig);
                        UI.getCurrent().getPage().reload();
                    });
        });
    }

    private boolean isVisible(final MidasConfig config) {
        return !config.getUi().isHideLanguageSelector() || config.getI18n().isForceDefaultLanguage();
    }
}
