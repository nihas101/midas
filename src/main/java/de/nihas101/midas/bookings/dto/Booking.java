package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.bookings.entity.BookingEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private Integer id;
    private Integer displayId;
    private Integer shareholderId;
    private LocalDate date;
    private BookingType type;
    private MoneyAmount amount;
    private String comment;

    public static Booking fromEntity(BookingEntity entity) {
        if (entity == null) {
            return null;
        }

        return Booking.builder()
                .id(entity.getId())
                .displayId(entity.getDisplayId() != null ? entity.getDisplayId() : entity.getId())
                .shareholderId(entity.getShareholder() != null ? entity.getShareholder().getId() : null)
                .date(entity.getDate())
                .type(entity.getType())
                .amount(entity.getAmount())
                .comment(entity.getComment())
                .build();
    }
}
