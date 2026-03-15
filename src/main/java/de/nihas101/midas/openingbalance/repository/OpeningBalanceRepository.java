package de.nihas101.midas.openingbalance.repository;

import de.nihas101.midas.openingbalance.entity.OpeningBalanceEntity;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface OpeningBalanceRepository extends JpaRepository<OpeningBalanceEntity, Integer> {
    Optional<OpeningBalanceEntity> findByShareholderAndDate(ShareholderEntity shareholder, LocalDate date);
}
