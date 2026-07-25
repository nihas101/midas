package de.nihas101.midas.lock.repository;

import de.nihas101.midas.lock.entity.LockEntity;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LockRepository extends JpaRepository<LockEntity, Integer> {

    boolean existsByShareholderAndYear(ShareholderEntity shareholder, Integer year);

    Optional<LockEntity> findByShareholderAndYear(ShareholderEntity shareholder, Integer year);
}
