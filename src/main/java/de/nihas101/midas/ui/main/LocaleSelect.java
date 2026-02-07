package de.nihas101.midas.ui.main;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.i18n.I18NProvider;
import de.nihas101.midas.config.MidasConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;

@Slf4j
public class LocaleSelect extends Select<Locale> {

    public LocaleSelect(
            I18NProvider i18NProvider,
            final Locale locale,
            MidasConfig config
    ) {
        final List<Locale> providedLocales = i18NProvider.getProvidedLocales();
        this.setVisible(!config.getUi().isHideLanguageSelector());
        this.setItems(providedLocales);
        this.setItemLabelGenerator(loc -> loc.getDisplayName(loc));
        this.setValue(locale);
        this.addValueChangeListener(event -> {
            Locale selectedLocale = event.getValue();
            if (selectedLocale == null) {
                return;
            }
            UI.getCurrent().setLocale(selectedLocale);
            this.getUI()
                    .map(UI::getPage)
                    .ifPresent(p -> p.executeJs("localStorage.setItem('locale', '%s');".formatted(selectedLocale)));
            UI.getCurrent().getPage().reload();
        });
    }
}
