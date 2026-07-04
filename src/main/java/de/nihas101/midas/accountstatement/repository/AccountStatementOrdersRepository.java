package de.nihas101.midas.accountstatement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountStatementOrdersRepository extends JpaRepository<AccountStatementOrderEntity, Integer> {

    List<AccountStatementOrderEntity> findByShareholderIdAndYearOrderByPositionAsc(Integer shareholderId, Integer year);

    Optional<AccountStatementOrderEntity> findByShareholderIdAndYearAndRowKey(Integer shareholderId, Integer year, String rowKey);

    void deleteByShareholderIdAndYear(Integer shareholderId, Integer year);
}
