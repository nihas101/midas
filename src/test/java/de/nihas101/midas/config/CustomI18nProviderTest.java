package de.nihas101.midas.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class CustomI18nProviderTest {

    @Mock
    private MessageSource messageSource;

    @Test
    void getProvidedLocales() {
        final CustomI18nProvider i18nProvider = new CustomI18nProvider(messageSource);
        final List<Locale> providedLocales = i18nProvider.getProvidedLocales();
        Assertions.assertNotNull(providedLocales);
        Assertions.assertFalse(providedLocales.isEmpty());
    }

    @Test
    void getTranslation() {
        final CustomI18nProvider i18nProvider = new CustomI18nProvider(messageSource);
        final String key = "key";
        final Locale locale = Locale.ENGLISH;
        final Object[] objects = new Object[]{new Object()};
        final String expected = "translated";
        Mockito.when(messageSource.getMessage(key, objects, locale))
                .thenReturn(expected);

        final String translation = i18nProvider.getTranslation(key, locale, objects);

        Assertions.assertEquals(expected, translation);
    }
}