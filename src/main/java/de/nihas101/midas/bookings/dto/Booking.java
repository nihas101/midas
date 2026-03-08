package de.nihas101.midas.bookings.dto;

import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.bookings.entity.BookingEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    private Integer id;
    private Integer displayId;
    private Integer shareholderId;
    private LocalDate date;
    private BookingType type;
    private MoneyAmount amount;
    private String comment;

    public static Booking fromEntity(BookingEntity entity) {
        return Booking.builder()
                .id(entity.getId())
                .displayId(entity.getDisplayId() != null ? entity.getDisplayId() : entity.getId())
                .shareholderId(entity.getShareholder().getId())
                .date(entity.getDate())
                .type(entity.getType())
                .amount(entity.getAmount())
                .comment(entity.getComment())
                .build();
    }
}
