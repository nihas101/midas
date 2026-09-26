package de.nihas101.midas.headless.config;

import de.nihas101.midas.commons.MoneyAmount;
import org.springframework.stereotype.Component;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

@Component
class MoneyAmountValueSerializer extends ValueSerializer<MoneyAmount> {
    @Override
    public void serialize(
            final MoneyAmount value,
            final JsonGenerator gen,
            final SerializationContext serializers
    ) {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeNumber(value.toBigDecimalForInput());
        }
    }
}
