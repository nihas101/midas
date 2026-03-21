package de.nihas101.midas.bookings.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BookingTypeConverter implements AttributeConverter<BookingType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(BookingType bookingType) {
        return bookingType == null ? null : bookingType.getId();
    }

    @Override
    public BookingType convertToEntityAttribute(Integer bookingTypeId) {
        return bookingTypeId == null ? null : BookingType.fromId(bookingTypeId);
    }
}
