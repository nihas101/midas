package de.nihas101.midas.core.bookings.dto;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.persistance.bookings.BookingEntity;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultBooking implements Booking {

    private Integer id;
    private Integer displayId;
    private Integer shareholderId;
    private LocalDate date;
    private BookingType type;
    private MoneyAmount amount;
    private String comment;
    private Source source;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Integer getDisplayId() {
        return displayId;
    }

    @Override
    public Integer getShareholderId() {
        return shareholderId;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public BookingType getType() {
        return type;
    }

    @Override
    public MoneyAmount getAmount() {
        return amount;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public Source getSource() {
        return source;
    }

    @Override
    public void setId(final int id) {
        this.id = id;
    }

    @Override
    public void setShareholderId(final Integer shareholderId) {
        this.shareholderId = shareholderId;
    }

    @Override
    public void setDate(final LocalDate date) {
        this.date = date;
    }

    @Override
    public void setType(final BookingType type) {
        this.type = type;
    }

    @Override
    public void setAmount(final MoneyAmount amount) {
        this.amount = amount;
    }

    @Override
    public void setComment(final String comment) {
        this.comment = comment;
    }

    @Override
    public void setSource(final Source source) {
        this.source = source;
    }

    public static Booking fromEntity(BookingEntity entity) {
        if (entity == null) {
            return null;
        }

        return DefaultBooking.builder()
                .id(entity.getId())
                .displayId(entity.getDisplayId() != null ? entity.getDisplayId() : entity.getId())
                .shareholderId(entity.getShareholder() != null ? entity.getShareholder().getId() : null)
                .date(entity.getDate())
                .type(entity.getType())
                .amount(entity.getAmount())
                .comment(entity.getComment())
                .source(entity.getSource())
                .build();
    }

    public static BookingEntity fromDto(
            final Booking booking,
            final ShareholderEntity shareholder
    ) {
        if (booking == null) {
            return null;
        }

        return new BookingEntity(
                booking.getId(),
                booking.getDisplayId(),
                shareholder,
                booking.getDate(),
                booking.getType(),
                booking.getAmount(),
                booking.getComment(),
                booking.getSource()
        );
    }
}
