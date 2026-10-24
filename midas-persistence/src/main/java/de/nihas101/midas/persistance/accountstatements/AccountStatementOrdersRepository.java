package de.nihas101.midas.persistance.accountstatements;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountStatementOrdersRepository extends JpaRepository<AccountStatementOrderEntity, Integer> {

    List<AccountStatementOrderEntity> findByShareholderIdAndYearOrderByPositionAsc(Integer shareholderId, Integer year);

    Optional<AccountStatementOrderEntity> findByShareholderIdAndYearAndRowKey(Integer shareholderId, Integer year, String rowKey);

    @Modifying
    @Transactional
    void deleteByShareholderIdAndYear(Integer shareholderId, Integer year);

    // Remove statement ordering for years that cannot be edited anymore
    @Modifying
    @Transactional
    @Query(
            value = """
                    DELETE FROM account_statement_orders
                    WHERE id IN (
                        SELECT aso.id FROM account_statement_orders aso
                        WHERE aso.year < :cutoffYear
                    )
                    """,
            nativeQuery = true
    )
    int deleteOrphanedStatementOrders(int cutoffYear);
}
