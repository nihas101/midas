package de.nihas101.midas.bookings.service;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.entity.BookingEntity;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.entity.Source;
import de.nihas101.midas.bookings.repository.BookingsRepository;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.repository.OpeningBalanceRepository;
import de.nihas101.midas.shareholders.entity.ShareholderEntity;
import de.nihas101.midas.shareholders.repository.ShareholdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingsServiceTest {

    @Mock
    private BookingsRepository bookingsRepository;

    @Mock
    private ShareholdersRepository shareholdersRepository;

    @InjectMocks
    private BookingsService bookingsService;

    @Mock
    private OpeningBalanceRepository openingBalanceRepository;

    private ShareholderEntity shareholder;

    @BeforeEach
    void setUp() {
        shareholder = new ShareholderEntity();
        shareholder.setId(1);
    }

    @Test
    void bookingsForShareholderAndYear_success() {
        // Arrange
        Year year = Year.of(2026);
        when(shareholdersRepository.findById(1)).thenReturn(Optional.of(shareholder));

        BookingEntity entity = new BookingEntity(
                10,
                null,
                shareholder,
                year.atMonth(Month.MARCH).atDay(11),
                BookingType.WITHDRAWAL,
                MoneyAmount.ofCents(1000L),
                null,
                Source.USER
        );

        when(bookingsRepository.findByShareholderAndDateBetweenOrderByDateAsc(
                eq(shareholder),
                eq(year.atMonth(Month.JANUARY).atDay(1)),
                eq(year.atMonth(Month.DECEMBER).atDay(31)))
        ).thenReturn(List.of(entity));

        // Act
        Bookings result = bookingsService.bookingsForShareholderAndYear(1, year);

        // Assert
        assertNotNull(result);
        assertEquals(MoneyAmount.ZERO, result.openingBalance().getOpeningBalance());
        assertEquals(1, result.bookingsInMonth(Month.MARCH).bookings().size());
    }

    @Test
    void bookingsForShareholderAndYear_shareholderNotFound() {
        when(shareholdersRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bookingsService.bookingsForShareholderAndYear(1, Year.of(2026)));
    }

    @Test
    void create_success() {
        // Arrange
        Booking dto = Booking.builder()
                .shareholderId(1)
                .date(LocalDate.now())
                .type(BookingType.WITHDRAWAL)
                .amount(MoneyAmount.ofCents(500L))
                .comment("New")
                .build();

        when(shareholdersRepository.findById(1)).thenReturn(Optional.of(shareholder));

        // Act
        bookingsService.create(dto);

        // Assert
        ArgumentCaptor<BookingEntity> captor = ArgumentCaptor.forClass(BookingEntity.class);
        verify(bookingsRepository).save(captor.capture());
        BookingEntity saved = captor.getValue();

        assertNull(saved.getId());
        assertEquals(shareholder, saved.getShareholder());
        assertEquals(BookingType.WITHDRAWAL, saved.getType());
        assertEquals(MoneyAmount.ofCents(500L), saved.getAmount());
    }

    @Test
    void create_withIdFails() {
        Booking dto = Booking.builder().id(1).build();
        assertThrows(IllegalArgumentException.class, () -> bookingsService.create(dto));
    }

    @Test
    void update_success() {
        // Arrange
        final LocalDate now = LocalDate.now();
        final MoneyAmount amount = MoneyAmount.ofCents(200L);
        Booking dto = Booking.builder()
                .id(10)
                .shareholderId(1)
                .date(now)
                .type(BookingType.INTEREST)
                .amount(amount)
                .source(Source.USER)
                .build();

        when(shareholdersRepository.findById(1)).thenReturn(Optional.of(shareholder));

        // Act
        bookingsService.update(dto);

        // Assert
        BookingEntity expectedSave = BookingEntity.builder()
                .id(10)
                .shareholder(new ShareholderEntity(1, null, null, null))
                .type(BookingType.INTEREST)
                .date(now)
                .amount(amount)
                .source(Source.USER)
                .build();
        verify(bookingsRepository).save(expectedSave);
        assertEquals(BookingType.INTEREST, expectedSave.getType());
        assertEquals(amount, expectedSave.getAmount());
    }

    @Test
    void update_withoutIdFails() {
        Booking dto = Booking.builder().id(null).build();
        assertThrows(IllegalArgumentException.class, () -> bookingsService.update(dto));
    }
}
