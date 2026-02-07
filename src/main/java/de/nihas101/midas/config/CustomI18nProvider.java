package de.nihas101.midas.config;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class CustomI18nProvider implements I18NProvider {

    private final MessageSource messageSource;

    public CustomI18nProvider(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return Arrays.asList(
                Locale.ENGLISH,
                Locale.GERMAN
        );
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        return messageSource.getMessage(key, params, locale);
    }

}
