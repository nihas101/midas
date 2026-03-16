package de.nihas101.midas.money;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MoneyAmountConverter implements AttributeConverter<MoneyAmount, Long> {

    @Override
    public Long convertToDatabaseColumn(MoneyAmount amount) {
        return amount == null ? null : amount.getCents();
    }

    @Override
    public MoneyAmount convertToEntityAttribute(Long cents) {
        return cents == null ? MoneyAmount.ZERO : MoneyAmount.ofCents(cents);
    }
}
