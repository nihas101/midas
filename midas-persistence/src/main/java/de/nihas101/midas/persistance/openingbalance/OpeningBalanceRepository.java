package de.nihas101.midas.persistance.openingbalance;

import de.nihas101.midas.commons.Source;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface OpeningBalanceRepository extends JpaRepository<OpeningBalanceEntity, Integer> {
    Optional<OpeningBalanceEntity> findByShareholderAndDate(ShareholderEntity shareholder, LocalDate date);

    Optional<OpeningBalanceEntity> findFirstByShareholderAndDateAndSource(
            final ShareholderEntity shareholderEntity,
            final LocalDate localDate,
            final Source source
    );
}
