package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.Source;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(
            description = "Internal unique ID of the booking",
            example = "42"
    )
    private Integer id;

    @NotNull
    @Positive
    @Schema(
            description = "Display ID of the booking",
            example = "1"
    )
    private Integer displayId;

    @NotNull
    @Positive
    @Schema(
            description = "ID of the associated shareholder",
            example = "1"
    )
    private Integer shareholderId;

    @NotNull
    @Schema(
            description = "Date of the booking",
            example = "2026-05-15"
    )
    private LocalDate date;

    @NotNull
    @Schema(
            description = "Type of booking",
            example = "WITHDRAWAL"
    )
    private BookingType type;

    @NotNull
    @Schema(
            description = "Booking amount",
            example = "250.00"
    )
    private MoneyAmountDto amount;

    @Schema(
            description = "Comment or note for the booking",
            example = "Monthly withdrawal"
    )
    private String comment;

    @NotNull
    @Schema(
            description = "Source origin of the booking",
            example = "USER"
    )
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
