package de.nihas101.midas.core.export.pdf;

import de.nihas101.midas.api.bookings.BookingsReader;
import de.nihas101.midas.api.interest.InterestBookingsReader;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.commons.MoneyAmount;
import de.nihas101.midas.core.accountstatement.row.AccountStatementRowService;
import de.nihas101.midas.core.accountstatement.service.RunningTotalAccountStatementService;
import de.nihas101.midas.core.bookings.dto.DefaultBookings;
import de.nihas101.midas.core.bookings.row.BookingRowService;
import de.nihas101.midas.core.export.ExportRequest;
import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.export.ExportViews;
import de.nihas101.midas.core.interest.row.InterestRowService;
import de.nihas101.midas.core.interest.service.InterestRateService;
import de.nihas101.midas.core.openingbalance.dto.DefaultOpeningBalance;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfExporterTest {

    @Mock
    private ExportRequest request;

    @Mock
    private OutputStream outputStream;

    @Mock
    private PdfService pdfService;

    @Mock
    private BookingsReader bookingsReader;

    @Mock
    private InterestBookingsReader interestBookingsReader;

    @Mock
    private InterestRateService interestRateService;

    @Mock
    private RunningTotalAccountStatementService runningTotalAccountStatementService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private BookingRowService bookingRowService;

    @Mock
    private AccountStatementRowService accountStatementRowService;

    @Mock
    private InterestRowService interestRowService;

    @Mock
    private Shareholder shareholder;

    @ParameterizedTest
    @MethodSource("noFileCases")
    void constructor_noFile_triggersException(final List<Shareholder> shareholders, final Set<ExportViewName> views) {
        when(request.shareholders()).thenReturn(shareholders);
        when(request.views()).thenReturn(new ExportViews(views));

        Assertions.assertThrows(IllegalArgumentException.class, () -> new PdfExporter(
                request,
                outputStream,
                Locale.GERMAN,
                pdfService,
                bookingsReader,
                interestBookingsReader,
                interestRateService,
                runningTotalAccountStatementService,
                messageSource,
                bookingRowService,
                accountStatementRowService,
                interestRowService
        ));
    }

    public static Stream<Arguments> noFileCases() {
        return Stream.of(
                Arguments.of(List.of(), Set.of(ExportViewName.BOOKINGS)),
                Arguments.of(List.of(new DefaultShareholder()), Set.of()),
                Arguments.of(List.of(), Set.of())
        );
    }

    @Test
    void trigger_singleFile_usesSinglePdfGeneratorAndCallsPdfServiceOnce() {
        when(request.shareholders()).thenReturn(List.of(shareholder));
        when(request.views()).thenReturn(new ExportViews(Set.of(ExportViewName.BOOKINGS)));
        when(request.startDate()).thenReturn(LocalDate.now());
        when(messageSource.getMessage(any(), any(), any())).thenReturn("dummy");
        new PdfExporter(
                request,
                outputStream,
                Locale.GERMAN,
                pdfService,
                bookingsReader,
                interestBookingsReader,
                interestRateService,
                runningTotalAccountStatementService,
                messageSource,
                bookingRowService,
                accountStatementRowService,
                interestRowService
        ).trigger();
        verify(pdfService, times(1)).generatePdf(any(), any(), any());
    }

    @Test
    void trigger_multiFile_usesMultiPdfGeneratorAndCallsPdfServiceForEachFile() {
        final Shareholder shareholder2 = mock(DefaultShareholder.class);
        when(request.shareholders()).thenReturn(List.of(shareholder, shareholder2));
        when(request.views()).thenReturn(new ExportViews(Set.of(ExportViewName.BOOKINGS, ExportViewName.INTEREST)));
        when(request.startDate()).thenReturn(Year.of(2026).atMonth(Month.JANUARY).atDay(1));
        when(request.endDate()).thenReturn(Year.of(2026).atMonth(Month.DECEMBER).atEndOfMonth());
        when(shareholder.getFirstName()).thenReturn("John");
        when(shareholder.getLastName()).thenReturn("Doe");
        when(shareholder2.getFirstName()).thenReturn("Jane");
        when(shareholder2.getLastName()).thenReturn("Smith");
        when(messageSource.getMessage(any(), any(), any())).thenReturn("dummy");
        when(interestBookingsReader.interestRelatedBookingsForShareholderAndYear(anyInt(), any()))
                .thenReturn(new DefaultBookings(Collections.emptyList(), new DefaultOpeningBalance(MoneyAmount.ZERO)));

        new PdfExporter(
                request,
                outputStream,
                Locale.US,
                pdfService,
                bookingsReader,
                interestBookingsReader,
                interestRateService,
                runningTotalAccountStatementService,
                messageSource,
                bookingRowService,
                accountStatementRowService,
                interestRowService
        ).trigger();

        verify(pdfService, times(4)).generatePdf(any(), any(), any());
    }
}