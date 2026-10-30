package de.nihas101.midas.headless.config;

import de.nihas101.midas.commons.MoneyAmount;
import org.springframework.stereotype.Component;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.math.BigDecimal;

@Component
class MoneyAmountValueDeserializer extends ValueDeserializer<MoneyAmount> {
    @Override
    public MoneyAmount deserialize(
            final JsonParser p,
            final DeserializationContext ctxt
    ) {
        final BigDecimal decimal = p.getDecimalValue();
        return MoneyAmount.of(decimal);
    }
}
