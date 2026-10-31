package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.Source;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBooking {

    @NotNull
    @Positive
    private Integer id;

    @NotNull
    @Positive
    private Integer displayId;

    @NotNull
    @Positive
    private Integer shareholderId;

    @NotNull
    private LocalDate date;

    @NotNull
    private BookingType type;

    @NotNull
    private MoneyAmountDto amount;

    private String comment;

    @NotNull
    private Source source;

    public static ResponseBooking toDto(final Booking booking) {
        return ResponseBooking.builder()
                .id(booking.getId())
                .displayId(booking.getDisplayId())
                .shareholderId(booking.getShareholderId())
                .date(booking.getDate())
                .type(booking.getType())
                .amount(MoneyAmountDto.fromDomain(booking.getAmount()))
                .comment(booking.getComment())
                .source(booking.getSource())
                .build();
    }
}
