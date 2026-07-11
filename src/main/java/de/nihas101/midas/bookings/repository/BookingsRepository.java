package de.nihas101.midas.bookings.repository;

import de.nihas101.midas.bookings.entity.BookingEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.entity.Source;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingsRepository extends JpaRepository<BookingEntity, Integer> {

    List<BookingEntity> findByShareholderAndDateBetweenOrderByDateAsc(
            final ShareholderEntity shareholder,
            final LocalDate start,
            final LocalDate end
    );

    BookingEntity findFirstByShareholderAndDateAndTypeAndSource(
            final ShareholderEntity shareholder,
            final LocalDate date,
            final BookingType bookingType,
            final Source source
    );

    boolean existsByShareholderAndDateAndTypeAndCommentAndIdNot(
            final ShareholderEntity shareholder,
            final LocalDate date,
            final BookingType bookingType,
            final String comment,
            final Integer id
    );

    @Modifying
    @Transactional
    void deleteByShareholderAndDateAndSource(
            final ShareholderEntity shareholder,
            final LocalDate date,
            final Source source
    );

    /*
     * DELETE does not support ORDER and LIMIT, unless it was compiled with `SQLITE_ENABLE_UPDATE_DELETE_LIMIT`,
     * so we use a subquery here to support this functionality in either case.
     * https://www.sqlite.org/compile.html#enable_update_delete_limit
     */
    @Modifying
    @Transactional
    @Query(
            value = """
                    DELETE FROM bookings
                    WHERE id IN (
                        SELECT id FROM bookings
                        WHERE date < :cutoff
                        ORDER BY date
                        LIMIT :limit
                    );
                    """,
            nativeQuery = true
    )
    int deleteBeforeWithLimit(final LocalDate cutoff, final int limit);

    @Modifying
    @Transactional
    @Query(
            value = """
                    DELETE from bookings b
                    WHERE b.date < :cutoff
                    """,
            nativeQuery = true
    )
    int deleteBefore(final LocalDate cutoff);
}
