package de.nihas101.midas.core.interest.service;

import de.nihas101.midas.api.bookings.Booking;
import de.nihas101.midas.api.bookings.Bookings;
import de.nihas101.midas.api.bookings.BookingsWriter;
import de.nihas101.midas.api.bookings.FilteredBookings;
import de.nihas101.midas.api.interest.InterestBookingsReader;
import de.nihas101.midas.api.openingbalance.OpeningBalance;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.commons.BookingType;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.commons.Source;
import de.nihas101.midas.core.bookings.dto.DefaultBooking;
import de.nihas101.midas.core.interest.service.bookingupdate.DefaultInterestUpdatingBookingsService;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import de.nihas101.midas.persistance.interest.InterestRateEntity;
import de.nihas101.midas.persistance.interest.InterestRateRepository;
import de.nihas101.midas.persistance.shareholders.ShareholderEntity;
import de.nihas101.midas.persistance.shareholders.ShareholdersRepository;
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

    private DefaultInterestUpdatingBookingsService service;

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

        booking = new DefaultBooking(
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
                return new DefaultOpeningBalance(MoneyAmount.ofCents(100L));
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
        interestBooking = new DefaultBooking(
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

        service = new DefaultInterestUpdatingBookingsService(
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