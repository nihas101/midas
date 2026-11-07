package de.nihas101.midas.headless.rest.dto;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.bookings.dto.DefaultBooking;
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
public class RequestBooking {

    @Positive
    @Schema(description = "Optional display ID of the booking", example = "1")
    private Integer displayId;

    @NotNull
    @Positive
    @Schema(description = "ID of the associated shareholder", example = "1")
    private Integer shareholderId;

    @NotNull
    @Schema(description = "Date of the booking", example = "2026-05-15")
    private LocalDate date;

    @NotNull
    @Schema(description = "Type of booking", example = "WITHDRAWAL")
    private BookingType type;

    @NotNull
    @Schema(description = "Booking amount", example = "250.00")
    private MoneyAmountDto amount;

    @Schema(description = "Optional comment or note for the booking", example = "Monthly withdrawal")
    private String comment;

    @Schema(description = "Source origin of the booking", example = "USER")
    private Source source;

    public Source getSource() {
        return source != null ? source : Source.USER;
    }

    private DefaultBooking.DefaultBookingBuilder baseBooking() {
        return DefaultBooking.builder()
                .displayId(this.getDisplayId())
                .shareholderId(this.getShareholderId())
                .date(this.getDate())
                .type(this.getType())
                .amount(this.getAmount() != null ? this.getAmount().toDomain() : null)
                .comment(this.getComment())
                .source(this.getSource());
    }

    public Booking toDomain(final Integer id) {
        return baseBooking().id(id).build();
    }

    public Booking toDomain() {
        return baseBooking().build();
    }
}
