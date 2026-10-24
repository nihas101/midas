package de.nihas101.midas.persistance.accountstatements;

import de.nihas101.midas.commons.BookingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountStatementOverridesRepository extends JpaRepository<AccountStatementOverrideEntity, Integer> {

    List<AccountStatementOverrideEntity> findByShareholderIdAndYear(Integer shareholderId, Integer year);

    Optional<AccountStatementOverrideEntity> findByShareholderIdAndYearAndBookingType(
            Integer shareholderId,
            Integer year,
            BookingType bookingType
    );

    // Remove statement ordering for years that cannot be edited anymore
    @Modifying
    @Transactional
    @Query(
            value = """
                    DELETE FROM account_statement_overrides
                    WHERE id IN (
                        SELECT aso.id FROM account_statement_orders aso
                        WHERE aso.year < :cutoffYear
                    )
                    """,
            nativeQuery = true
    )
    int deleteOrphanedStatementOverrides(int cutoffYear);
}
