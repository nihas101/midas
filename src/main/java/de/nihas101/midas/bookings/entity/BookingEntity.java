package de.nihas101.midas.bookings.entity;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.money.MoneyAmount;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "display_id", insertable = false)
    @Generated(event = {EventType.INSERT})
    private Integer displayId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shareholder_id", nullable = false)
    private ShareholderEntity shareholder;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "type", nullable = false)
    private BookingType type;

    @Column(name = "amount", nullable = false)
    private MoneyAmount amount = MoneyAmount.ZERO;

    @Column(name = "comment")
    private String comment;

    public static BookingEntity fromDto(final Booking booking, final ShareholderEntity shareholder) {
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
                booking.getComment()
        );
    }
}
