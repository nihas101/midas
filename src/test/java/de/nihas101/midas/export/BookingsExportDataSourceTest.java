package de.nihas101.midas.export;

import de.nihas101.midas.bookings.dto.Booking;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.FilteredBookings;
import de.nihas101.midas.bookings.entity.BookingType;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.export.bookings.BookingsExportDataSource;
import de.nihas101.midas.export.bookings.BookingsRowExtractor;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.openingbalance.service.OpeningBalanceService;
import de.nihas101.midas.shareholders.dto.Shareholder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingsExportDataSourceTest {

    @Mock
    private BookingsReader bookingsReader;

    @Mock
    private OpeningBalanceService openingBalanceService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ExportTarget exportTarget;

    private BookingsExportDataSource bookingsExportDataSource;

    private final Locale locale = Locale.GERMAN;
    private final LocalDate startDate = LocalDate.of(2023, 1, 1);
    private final LocalDate endDate = LocalDate.of(2023, 12, 31);
    private final Shareholder alice = new Shareholder(1, 1, "Alice", "A");
    private final Shareholder bob = new Shareholder(2, 2, "Bob", "B");

    @BeforeEach
    void setUp() {
        bookingsExportDataSource = new BookingsExportDataSource(
                new BookingsRowExtractor(
                        List.of(alice, bob),
                        startDate,
                        endDate,
                        bookingsReader,
                        openingBalanceService,
                        messageSource,
                        locale
                ),
                messageSource,
                locale
        );
        lenient().when(messageSource.getMessage(any(), any(), any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void export_callsTargetWithCorrectData() {
        // Given
        // Alice: 1 Opening Balance, 1 Booking
        OpeningBalance aliceOb = new OpeningBalance(1, 1, MoneyAmount.of(new BigDecimal("1000.00")), Year.of(2023));
        when(openingBalanceService.openingBalance(eq(1), eq(Year.of(2023)))).thenReturn(aliceOb);

        Bookings aliceBookings = mock(Bookings.class);
        Booking aliceB1 = Booking.builder().id(101).date(LocalDate.of(2023, 5, 10)).type(BookingType.WITHDRAWAL).amount(MoneyAmount.of(new BigDecimal("-100.00"))).comment("Lunch").build();
        when(aliceBookings.filter(any())).thenReturn(new FilteredBookings(List.of(aliceB1)));
        when(bookingsReader.bookingsForShareholderAndYear(eq(1), eq(Year.of(2023)))).thenReturn(aliceBookings);

        // Bob: No Opening Balance, 1 Booking
        when(openingBalanceService.openingBalance(eq(2), any())).thenReturn(null);
        Bookings bobBookings = mock(Bookings.class);
        Booking bobB1 = Booking.builder().id(201).date(LocalDate.of(2023, 6, 15)).type(BookingType.INTEREST).amount(MoneyAmount.of(new BigDecimal("5.50"))).comment("Interest June").build();
        when(bobBookings.filter(any())).thenReturn(new FilteredBookings(List.of(bobB1)));
        when(bookingsReader.bookingsForShareholderAndYear(eq(2), eq(Year.of(2023)))).thenReturn(bobBookings);

        // When
        bookingsExportDataSource.export(exportTarget);

        // Then
        ArgumentCaptor<List<List<Object>>> rowsCaptor = ArgumentCaptor.forClass(List.class);
        verify(exportTarget).export(eq("bookings"), any(), rowsCaptor.capture());

        List<List<Object>> rows = rowsCaptor.getValue();
        assertEquals(3, rows.size());

        // Verify Alice's rows (Sorted by name)
        // Alice OB (01.01.2023)
        List<Object> row1 = rows.getFirst();
        assertEquals("Alice A", row1.get(0));
        assertEquals("", row1.get(1));
        assertEquals(LocalDate.of(2023, 1, 1), row1.get(2));
        assertEquals(0, ((BigDecimal) row1.get(9)).compareTo(new BigDecimal("1000.00"))); // OB column

        // Alice Booking (10.05.2023)
        List<Object> row2 = rows.get(1);
        assertEquals("Alice A", row2.get(0));
        assertEquals(101, row2.get(1));
        assertEquals(0, ((BigDecimal) row2.get(4)).compareTo(new BigDecimal("-100.00"))); // Withdrawal column

        // Bob Booking (15.06.2023)
        List<Object> row3 = rows.get(2);
        assertEquals("Bob B", row3.get(0));
        assertEquals(0, ((BigDecimal) row3.get(7)).compareTo(new BigDecimal("5.50"))); // Interest column
    }

    @Test
    void export_filtersByDateRange() {
        // Given
        bookingsExportDataSource = new BookingsExportDataSource(
                new BookingsRowExtractor(
                        List.of(alice),
                        LocalDate.of(2023, 6, 1),
                        LocalDate.of(2023, 6, 30),
                        bookingsReader,
                        openingBalanceService,
                        messageSource,
                        locale
                ),
                messageSource,
                locale
        );

        // OB is 01.01.2023 -> should be excluded
        OpeningBalance ob = new OpeningBalance(1, 1, MoneyAmount.of(new BigDecimal("100.00")), Year.of(2023));
        when(openingBalanceService.openingBalance(any(), any())).thenReturn(ob);

        Bookings bookings = mock(Bookings.class);
        Booking bIn = Booking.builder().id(1).date(LocalDate.of(2023, 6, 15)).type(BookingType.WITHDRAWAL).amount(MoneyAmount.ZERO).build();
        when(bookings.filter(any())).thenReturn(new FilteredBookings(List.of(bIn)));
        when(bookingsReader.bookingsForShareholderAndYear(any(), any())).thenReturn(bookings);

        // When
        bookingsExportDataSource.export(exportTarget);

        // Then
        ArgumentCaptor<List<List<Object>>> rowsCaptor = ArgumentCaptor.forClass(List.class);
        verify(exportTarget).export(any(), any(), rowsCaptor.capture());
        assertEquals(1, rowsCaptor.getValue().size());
        assertEquals(1, rowsCaptor.getValue().getFirst().get(1)); // Only bIn remains
    }

    @Test
    void export_withMultipleYears_iteratesCorrectly() {
        // Given
        bookingsExportDataSource = new BookingsExportDataSource(
                new BookingsRowExtractor(
                        List.of(alice),
                        LocalDate.of(2022, 12, 31),
                        LocalDate.of(2023, 1, 1),
                        bookingsReader,
                        openingBalanceService,
                        messageSource,
                        locale
                ),
                messageSource,
                locale
        );

        Bookings emptyBookings = mock(Bookings.class);
        when(emptyBookings.filter(any())).thenReturn(new FilteredBookings(List.of()));
        when(bookingsReader.bookingsForShareholderAndYear(any(), any())).thenReturn(emptyBookings);

        // When
        bookingsExportDataSource.export(exportTarget);

        // Then
        verify(bookingsReader).bookingsForShareholderAndYear(eq(alice.getId()), eq(Year.of(2022)));
        verify(bookingsReader).bookingsForShareholderAndYear(eq(alice.getId()), eq(Year.of(2023)));
    }
}
