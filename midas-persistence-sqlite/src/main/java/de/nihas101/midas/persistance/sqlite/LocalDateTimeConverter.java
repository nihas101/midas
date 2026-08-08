package de.nihas101.midas.persistance.sqlite;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Converter(autoApply = true)
@ConditionalOnProperty(
        name = "spring.datasource.driver-class-name",
        havingValue = "org.sqlite.JDBC"
)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Long> {

    @Override
    public Long convertToDatabaseColumn(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Long millis) {
        if (millis == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }
}
