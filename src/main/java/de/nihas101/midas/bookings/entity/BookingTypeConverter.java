package de.nihas101.midas.bookings.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BookingTypeConverter implements AttributeConverter<BookingType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(BookingType attribute) {
        return attribute == null ? null : attribute.getId();
    }

    @Override
    public BookingType convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : BookingType.fromId(dbData);
    }
}
