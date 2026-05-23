package de.nihas101.midas.export.pdf;

import de.nihas101.midas.accountstatement.row.AccountStatementRowService;
import de.nihas101.midas.accountstatement.service.AccountStatementService;
import de.nihas101.midas.bookings.row.BookingRowService;
import de.nihas101.midas.bookings.service.BookingsReader;
import de.nihas101.midas.export.ExportRequest;
import de.nihas101.midas.export.ExportViews;
import de.nihas101.midas.interest.row.InterestRowService;
import de.nihas101.midas.interest.service.InterestBookingsReader;
import de.nihas101.midas.interest.service.InterestRateService;
import de.nihas101.midas.shareholders.dto.Shareholder;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
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
    private AccountStatementService accountStatementService;

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
    void constructor_noFile_triggersException(final List<Shareholder> shareholders, final Set<String> views) {
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
                accountStatementService,
                messageSource,
                bookingRowService,
                accountStatementRowService,
                interestRowService
        ));
    }

    public static Stream<Arguments> noFileCases() {
        return Stream.of(
                Arguments.of(List.of(), Set.of("view1")),
                Arguments.of(List.of(new Shareholder()), Set.of()),
                Arguments.of(List.of(), Set.of())
        );
    }

    @Test
    void trigger_singleFile_usesSinglePdfGeneratorAndCallsPdfServiceOnce() {
        when(request.shareholders()).thenReturn(List.of(shareholder));
        when(request.views()).thenReturn(new ExportViews(Set.of("view1")));
        when(request.startDate()).thenReturn(LocalDate.now());
        PdfExporter exporter = new PdfExporter(
                request, outputStream, Locale.GERMAN, pdfService,
                bookingsReader, interestBookingsReader, interestRateService,
                accountStatementService, messageSource, bookingRowService,
                accountStatementRowService, interestRowService);
        exporter.trigger();
        verify(pdfService, times(1)).generatePdf(any(), any(), any());
    }

    @Test
    void trigger_multiFile_usesMultiPdfGeneratorAndCallsPdfServiceForEachFile() {
        Shareholder shareholder2 = mock(Shareholder.class);
        when(request.shareholders()).thenReturn(List.of(shareholder, shareholder2));
        when(request.views()).thenReturn(new ExportViews(Set.of("viewA", "viewB")));
        when(request.startDate()).thenReturn(Year.of(2026).atMonth(Month.JANUARY).atDay(1));
        when(request.endDate()).thenReturn(Year.of(2026).atMonth(Month.DECEMBER).atEndOfMonth());
        when(shareholder.getFirstName()).thenReturn("John");
        when(shareholder.getLastName()).thenReturn("Doe");
        when(shareholder2.getFirstName()).thenReturn("Jane");
        when(shareholder2.getLastName()).thenReturn("Smith");
        PdfExporter exporter = new PdfExporter(
                request, outputStream, Locale.US, pdfService,
                bookingsReader, interestBookingsReader, interestRateService,
                accountStatementService, messageSource, bookingRowService,
                accountStatementRowService, interestRowService);
        exporter.trigger();
        verify(pdfService, times(4)).generatePdf(any(), any(), any());
    }
}