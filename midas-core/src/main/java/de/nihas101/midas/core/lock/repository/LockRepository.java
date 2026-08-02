package de.nihas101.midas.core.lock.repository;

import de.nihas101.midas.core.lock.entity.LockEntity;
import de.nihas101.midas.core.shareholders.entity.ShareholderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LockRepository extends JpaRepository<LockEntity, Integer> {

    boolean existsByShareholderAndYear(ShareholderEntity shareholder, Integer year);

    Optional<LockEntity> findByShareholderAndYear(ShareholderEntity shareholder, Integer year);

    // Remove locks for years that cannot be edited anymore
    @Modifying
    @Transactional
    @Query(
            value = """
                    DELETE FROM locked_years
                    WHERE id IN (
                        SELECT ly.id FROM locked_years ly
                        WHERE ly.year < (:cutoffYear - 1)
                    )
                    """,
            nativeQuery = true
    )
    int deleteOrphanedLocks(int cutoffYear);
}
