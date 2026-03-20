package de.nihas101.midas.interest.entity;

import de.nihas101.midas.interest.dto.InterestRate;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "interest_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterestRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shareholder_id", nullable = false)
    private ShareholderEntity shareholder;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "rate", nullable = false)
    private Long rate;

    public static InterestRateEntity fromDto(final InterestRate interestRate, final ShareholderEntity shareholder) {
        return new InterestRateEntity(
                interestRate.getId(),
                shareholder,
                interestRate.getYear().atDay(1),
                interestRate.toEntity()
        );
    }
}
