package de.nihas101.midas.export.pdf;

import de.nihas101.midas.accountstatement.row.AccountStatementRow;
import de.nihas101.midas.accountstatement.row.AccountStatementRowService;
import de.nihas101.midas.accountstatement.runningtotal.RunningTotalAccountStatements;
import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.bookings.dto.Bookings;
import de.nihas101.midas.bookings.dto.FilteredBookings;
import de.nihas101.midas.bookings.row.BookingRow;
import de.nihas101.midas.bookings.row.BookingRowService;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.interest.dto.InterestRate;
import de.nihas101.midas.interest.row.InterestCalculationRow;
import de.nihas101.midas.interest.row.InterestRowService;
import de.nihas101.midas.interest.service.InterestBookingsReader;
import de.nihas101.midas.interest.service.InterestRateService;
import de.nihas101.midas.money.MoneyAmount;
import de.nihas101.midas.openingbalance.dto.OpeningBalance;
import de.nihas101.midas.shareholders.dto.Shareholder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Locale;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfViewDataExtractorTest {

    @Mock
    private ExportRequest request;

    @Mock
    private MessageSource messageSource;

    @Mock
    private Locale locale;

    @Mock
    private Bookings bookings;

    @Mock
    private BookingsReader bookingsReader;

    @Mock
    private BookingRowService bookingRowService;

    @Mock
    private AccountStatementService accountStatementService;

    @Mock
    private AccountStatementRowService accountStatementRowService;

    @Mock
    private InterestRateService interestRateService;

    @Mock
    private InterestBookingsReader interestBookingsReader;

    @Mock
    private InterestRowService interestRowService;

    @Mock
    private Shareholder shareholder;

    @InjectMocks
    private PdfViewDataExtractor extractor;

    @Test
    void extractData_bookingsView_returnsPdfViewDataWithBookings() {
        when(request.startDate()).thenReturn(Year.of(2026).atMonth(Month.JANUARY).atDay(1));
        when(request.endDate()).thenReturn(Year.of(2026).atMonth(Month.DECEMBER).atEndOfMonth());
        when(shareholder.getFirstName()).thenReturn("John");
        when(shareholder.getLastName()).thenReturn("Doe");
        when(shareholder.getId()).thenReturn(1);
        when(messageSource.getMessage(anyString(), any(), eq(locale))).thenReturn("dummy");

        when(bookingsReader.bookingsForShareholderAndDates(eq(1), any(), any()))
                .thenReturn(bookings);
        when(bookingRowService.generateRows(any(Bookings.class), eq(locale)))
                .thenReturn(List.of(mock(BookingRow.class), mock(BookingRow.class)));

        final PdfViewData result = extractor.extractData(shareholder, "bookings");
        assertEquals("bookings", result.viewName());
        assertEquals("John Doe", result.shareholderName());
        assertEquals(2026, result.year());
        assertNotNull(result.headers());
        assertEquals(10, result.headers().size());
        assertEquals(2, result.rows().size());
    }

    @Test
    void extractData_accountStatementsView_returnsPdfViewDataWithAccountStatements() {
        when(request.startDate()).thenReturn(Year.of(2026).atMonth(Month.JANUARY).atDay(1));
        when(shareholder.getFirstName()).thenReturn("John");
        when(shareholder.getLastName()).thenReturn("Doe");
        when(messageSource.getMessage(anyString(), any(), eq(locale))).thenReturn("dummy");

        RunningTotalAccountStatements mockRunningTotal = mock(RunningTotalAccountStatements.class);
        when(accountStatementService.runningTotalAccountStatements(eq(shareholder), any(Year.class), eq(messageSource), eq(locale)))
                .thenReturn(mockRunningTotal);
        when(accountStatementRowService.generateRows(mockRunningTotal)).thenReturn(List.of(mock(AccountStatementRow.class)));
        when(accountStatementRowService.generateClosingRow(mockRunningTotal, locale)).thenReturn(mock(AccountStatementRow.class));

        final PdfViewData result = extractor.extractData(shareholder, "account-statements");
        assertEquals("account-statements", result.viewName());
        assertEquals(6, result.headers().size());
        assertEquals(2, result.rows().size());
    }

    @Test
    void extractData_interestView_returnsPdfViewDataWithInterest() {
        when(request.startDate()).thenReturn(Year.of(2026).atMonth(Month.JANUARY).atDay(1));
        when(shareholder.getFirstName()).thenReturn("John");
        when(shareholder.getLastName()).thenReturn("Doe");
        when(shareholder.getId()).thenReturn(1);
        when(messageSource.getMessage(anyString(), any(), eq(locale))).thenReturn("dummy");

        final OpeningBalance openingBalance = mock(OpeningBalance.class);
        when(openingBalance.getOpeningBalance()).thenReturn(MoneyAmount.ZERO);
        when(bookings.openingBalance()).thenReturn(openingBalance);

        final FilteredBookings emptyFiltered = mock(FilteredBookings.class);
        when(emptyFiltered.bookings()).thenReturn(emptyList());
        when(bookings.bookingsInMonth(any())).thenReturn(emptyFiltered);

        final InterestRate mockRate = mock(InterestRate.class);
        when(interestRateService.interestRate(eq(1), any(Year.class))).thenReturn(mockRate);
        when(mockRate.getInterestRate()).thenReturn(new BigDecimal("5.0"));
        when(interestBookingsReader.interestRelatedBookingsForShareholderAndYear(eq(1), any(Year.class)))
                .thenReturn(bookings);
        when(interestRowService.generateRows(any(Year.class), eq(bookings), any(BigDecimal.class), any(), eq(locale)))
                .thenReturn(List.of(mock(InterestCalculationRow.class)));

        final PdfViewData result = extractor.extractData(shareholder, "interest");
        assertEquals("interest", result.viewName());
        assertEquals(new BigDecimal("5.0"), result.interestRate());
        assertEquals(7, result.headers().size());
        assertEquals(1, result.rows().size());
    }

    @Test
    void extractData_unknownView_returnsDefaultPdfViewData() {
        when(request.startDate()).thenReturn(Year.of(2026).atMonth(Month.JANUARY).atDay(1));
        when(shareholder.getFirstName()).thenReturn("John");
        when(shareholder.getLastName()).thenReturn("Doe");

        final PdfViewData result = extractor.extractData(shareholder, "unknown");
        assertEquals("unknown", result.viewName());
        assertEquals(2026, result.year());
        assertNull(result.interestRate());
        assertTrue(result.headers().isEmpty());
        assertTrue(result.rows().isEmpty());
    }
}