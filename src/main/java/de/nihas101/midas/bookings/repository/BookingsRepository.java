package de.nihas101.midas.bookings.repository;

import de.nihas101.midas.bookings.entity.BookingEntity;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingsRepository extends JpaRepository<BookingEntity, Integer> {

    List<BookingEntity> findByShareholderAndDateBetweenOrderByDateAsc(
            ShareholderEntity shareholder,
            LocalDate start,
            LocalDate end
    );
}
