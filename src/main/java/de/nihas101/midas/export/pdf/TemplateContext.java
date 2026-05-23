package de.nihas101.midas.export.pdf;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public record TemplateContext(Locale locale, Map<String, Object> variables) {

    public TemplateContext(final Locale locale) {
        this(locale, new HashMap<>());
    }

    public void setVariable(final String variable, final Object value) {
        this.variables.put(variable, value);
    }

    @Override
    public Map<String, Object> variables() {
        return new HashMap<>(variables);
    }
}
