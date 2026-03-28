package de.nihas101.midas.accountstatement.repository;

import de.nihas101.midas.bookings.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AccountStatementsRepository extends JpaRepository<BookingEntity, Integer> {

    @Query(
            value = "SELECT MAX(id) as id, date, type, SUM(amount) as amount " +
                    "FROM bookings " +
                    "WHERE shareholder_id = :shareholderId " +
                    "AND date BETWEEN :start AND :end " +
                    "GROUP BY type",
            nativeQuery = true
    )
    List<AccountStatementEntity> accountStatements(
            @Param("shareholderId") Integer shareholderId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

}
