package de.nihas101.midas.bookings.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter(autoApply = true)
public class SourceConverter implements AttributeConverter<Source, String> {

    @Override
    public String convertToDatabaseColumn(Source source) {
        return source == null ? null : source.getSource();
    }

    @Override
    public Source convertToEntityAttribute(String source) {
        if (source == null) {
            return Source.USER;
        }
        try {
            return Source.fromString(source);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown source: {}. Assuming USER as source.", source);
            return Source.USER;
        }
    }
}
