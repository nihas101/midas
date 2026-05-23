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
import de.nihas101.midas.export.ExportViews;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class PdfViewDataExtractorTest {

    @Mock
    ExportRequest request;

    @Mock
    MessageSource messageSource;

    @Mock
    Locale locale;

    @Mock
    Bookings bookings;

    @Mock
    BookingsReader bookingsReader;

    @Mock
    BookingRowService bookingRowService;

    @Mock
    AccountStatementService accountStatementService;

    @Mock
    AccountStatementRowService accountStatementRowService;

    @Mock
    InterestRateService interestRateService;

    @Mock
    InterestBookingsReader interestBookingsReader;

    @Mock
    InterestRowService interestRowService;

    @Mock
    Shareholder shareholder;

    @InjectMocks
    PdfViewDataExtractor extractor;

    private void setupCommonMocks() {
        when(request.shareholders()).thenReturn(List.of(shareholder));
        when(request.startDate()).thenReturn(Year.of(2026).atMonth(Month.JANUARY).atDay(1));
        when(request.endDate()).thenReturn(Year.of(2026).atMonth(Month.DECEMBER).atEndOfMonth());
        when(shareholder.getFirstName()).thenReturn("John");
        when(shareholder.getLastName()).thenReturn("Doe");
        when(shareholder.getId()).thenReturn(1);
        when(messageSource.getMessage(anyString(), any(), eq(locale))).thenReturn("dummy");

        final OpeningBalance openingBalance = mock(OpeningBalance.class);
        when(openingBalance.getOpeningBalance()).thenReturn(MoneyAmount.ZERO);
        when(bookings.openingBalance()).thenReturn(openingBalance);

        final FilteredBookings emptyFiltered = mock(FilteredBookings.class);
        when(emptyFiltered.bookings()).thenReturn(Collections.emptyList());
        when(bookings.bookingsInMonth(any())).thenReturn(emptyFiltered);
    }

    @Test
    void extractData_bookingsView_returnsPdfViewDataWithBookings() {
        setupCommonMocks();
        when(request.views()).thenReturn(new ExportViews(Set.of("bookings")));
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
        setupCommonMocks();
        when(request.views()).thenReturn(new ExportViews(Set.of("account-statements")));
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
        setupCommonMocks();
        when(request.views()).thenReturn(new ExportViews(Set.of("interest")));
        final InterestRate mockRate = mock(InterestRate.class);
        when(interestRateService.interestRate(eq(1), any(Year.class))).thenReturn(mockRate);
        when(mockRate.getInterestRate()).thenReturn(new BigDecimal("5.0"));
        when(bookingsReader.bookingsForShareholderAndDates(eq(1), any(), any())).thenReturn(bookings);
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
        setupCommonMocks();
        when(request.views()).thenReturn(new ExportViews(Set.of("unknown")));

        final PdfViewData result = extractor.extractData(shareholder, "unknown");
        assertEquals("unknown", result.viewName());
        assertEquals(2026, result.year());
        assertNull(result.interestRate());
        assertTrue(result.headers().isEmpty());
        assertTrue(result.rows().isEmpty());
    }
}