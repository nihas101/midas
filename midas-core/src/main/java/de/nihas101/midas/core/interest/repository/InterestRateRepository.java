package de.nihas101.midas.core.interest.repository;

import de.nihas101.midas.core.interest.entity.InterestRateEntity;
import de.nihas101.midas.core.shareholders.entity.ShareholderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface InterestRateRepository extends JpaRepository<InterestRateEntity, Integer> {
    Optional<InterestRateEntity> findByShareholderAndDate(ShareholderEntity shareholder, LocalDate date);
}
