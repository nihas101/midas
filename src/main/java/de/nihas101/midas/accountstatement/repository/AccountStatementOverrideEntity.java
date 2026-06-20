package de.nihas101.midas.accountstatement.repository;

import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.money.MoneyAmount;
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

@Entity
@Table(name = "account_statement_overrides")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementOverrideEntity {
    // TODO: Also implement re-ordering

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shareholder_id", nullable = false)
    private ShareholderEntity shareholder;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "booking_type")
    private BookingType bookingType;

    @Column(name = "label_override")
    private String labelOverride;

    @Column(name = "hidden")
    private Boolean hidden;

    @Column(name = "amount")
    private MoneyAmount amount;
}
