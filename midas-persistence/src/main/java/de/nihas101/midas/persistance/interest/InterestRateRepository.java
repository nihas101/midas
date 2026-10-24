package de.nihas101.midas.persistance.interest;

import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface InterestRateRepository extends JpaRepository<InterestRateEntity, Integer> {
    Optional<InterestRateEntity> findByShareholderAndDate(ShareholderEntity shareholder, LocalDate date);

    // Remove statement ordering for years that cannot be edited anymore
    @Modifying
    @Transactional
    @Query(
            value = """
                    DELETE FROM interest_rates
                    WHERE id IN (
                        SELECT ir.id FROM interest_rates ir
                        WHERE ir.date < :cutoffDate
                    )
                    """,
            nativeQuery = true
    )
    int deleteOrphanedInterestRates(final LocalDate cutoffDate);
}
