package de.nihas101.midas.persistance.shareholders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareholdersRepository extends JpaRepository<ShareholderEntity, Integer> {
}
