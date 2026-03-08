package de.nihas101.midas.shareholders.repository;

import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareholdersRepository extends JpaRepository<ShareholderEntity, Integer> {
}
