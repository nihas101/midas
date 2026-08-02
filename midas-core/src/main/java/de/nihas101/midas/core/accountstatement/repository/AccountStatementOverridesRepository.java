package de.nihas101.midas.core.accountstatement.repository;

import de.nihas101.midas.api.bookings.BookingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountStatementOverridesRepository extends JpaRepository<AccountStatementOverrideEntity, Integer> {

    List<AccountStatementOverrideEntity> findByShareholderIdAndYear(Integer shareholderId, Integer year);

    Optional<AccountStatementOverrideEntity> findByShareholderIdAndYearAndBookingType(
            Integer shareholderId,
            Integer year,
            BookingType bookingType
    );
}
