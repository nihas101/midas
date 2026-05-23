package de.nihas101.midas.interest.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.FilteredBookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.entity.Source;
import de.nihas101.midas.bookings.service.BookingsWriter;
import de.nihas101.midas.interest.entity.InterestRateEntity;
import de.nihas101.midas.interest.repository.InterestRateRepository;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.shareholders.dto.Shareholder;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterestUpdatingBookingsServiceTest {

    @Mock
    private BookingsWriter delegate;
    @Mock
    private InterestBookingsReader bookingsReader;
    @Mock
    private ShareholdersRepository shareholdersRepository;
    @Mock
    private InterestRateRepository interestRateRepository;

    private InterestUpdatingBookingsService service;

    private Booking booking;
    private Booking interestBooking;
    private ShareholderEntity shareholderEntity;
    private Bookings bookings;
    private InterestRateEntity rate;

    @BeforeEach
    void setUp() {
        shareholderEntity = new ShareholderEntity(
                1,
                1,
                "John",
                "Doe"
        );

        booking = new Booking(
                1,
                1,
                shareholderEntity.getId(),
                LocalDate.now(),
                BookingType.COMPENSATION,
                MoneyAmount.ofCents(100L),
                "comment",
                Source.USER
        );
        bookings = new Bookings() {
            @Override
            public OpeningBalance openingBalance() {
                return new OpeningBalance(MoneyAmount.ofCents(100L));
            }

            @Override
            public FilteredBookings bookingsInMonth(final Month month) {
                return new FilteredBookings(List.of(booking));
            }

            @Override
            public FilteredBookings filter(final Function<Booking, Boolean> condition) {
                return new FilteredBookings(List.of(booking));
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };

        when(shareholdersRepository.getReferenceById(shareholderEntity.getId()))
                .thenReturn(shareholderEntity);
        interestBooking = new Booking(
                2,
                2,
                1,
                LocalDate.now(),
                BookingType.INTEREST,
                MoneyAmount.ofCents(100L),
                "interest",
                Source.SYSTEM
        );

        rate = new InterestRateEntity(
                1,
                shareholderEntity,
                LocalDate.now(),
                5L
        );

        service = new InterestUpdatingBookingsService(
                delegate,
                bookingsReader,
                shareholdersRepository,
                interestRateRepository
        );
    }

    @Test
    void testCreateUpdatesInterest() {
        when(bookingsReader.systemGeneratedInterestForShareholderAndYear(any(Shareholder.class), any(Year.class)))
                .thenReturn(interestBooking);
        when(bookingsReader.interestRelatedBookingsForShareholderAndYear(eq(shareholderEntity.getId()), any(Year.class)))
                .thenReturn(bookings);
        when(interestRateRepository.findByShareholderAndDate(eq(shareholderEntity), any()))
                .thenReturn(Optional.of(rate));

        service.create(booking);

        verify(delegate).create(booking); // Verify delegate create for original booking
        verify(delegate).update(interestBooking); // Verify interest booking updated
    }

    @Test
    void testUpdateUpdatesInterest() {
        when(bookingsReader.systemGeneratedInterestForShareholderAndYear(any(Shareholder.class), any(Year.class)))
                .thenReturn(interestBooking);
        when(bookingsReader.interestRelatedBookingsForShareholderAndYear(eq(shareholderEntity.getId()), any(Year.class)))
                .thenReturn(bookings);
        when(interestRateRepository.findByShareholderAndDate(eq(shareholderEntity), any()))
                .thenReturn(Optional.of(rate));

        service.update(booking);

        verify(delegate).update(booking);
        verify(delegate).update(interestBooking);
    }

    @Test
    void testDeleteUpdatesInterest() {
        when(bookingsReader.systemGeneratedInterestForShareholderAndYear(any(Shareholder.class), any(Year.class)))
                .thenReturn(interestBooking);
        when(bookingsReader.interestRelatedBookingsForShareholderAndYear(eq(shareholderEntity.getId()), any(Year.class)))
                .thenReturn(bookings);
        when(interestRateRepository.findByShareholderAndDate(eq(shareholderEntity), any()))
                .thenReturn(Optional.of(rate));

        service.delete(booking);

        verify(delegate).delete(booking);
        verify(delegate).update(interestBooking);
    }

    @Test
    void testNoInterestBookingSkipsUpdate() {
        when(bookingsReader.systemGeneratedInterestForShareholderAndYear(any(Shareholder.class), any(Year.class)))
                .thenReturn(null);

        service.create(booking);

        // Only delegate create should be called, no further interactions
        verify(delegate).create(booking);
        verifyNoMoreInteractions(delegate);
    }
}