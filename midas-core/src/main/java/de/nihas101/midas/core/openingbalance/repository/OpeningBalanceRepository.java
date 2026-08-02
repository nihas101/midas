package de.nihas101.midas.core.openingbalance.repository;

import de.nihas101.midas.api.bookings.Source;
import de.nihas101.midas.core.openingbalance.entity.OpeningBalanceEntity;
import de.nihas101.midas.core.shareholders.entity.ShareholderEntity;
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
