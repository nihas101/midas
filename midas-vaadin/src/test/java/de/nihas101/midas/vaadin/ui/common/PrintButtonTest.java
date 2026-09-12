package de.nihas101.midas.vaadin.ui.common;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import de.nihas101.midas.api.export.Export;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.export.ExportFactory;
import de.nihas101.midas.core.export.ExportRequest;
import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.github.mvysny.kaributesting.v10.ButtonKt._click;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrintButtonTest {

    @Mock
    private MessageSource messageSource;
    @Mock
    private ExportFactory exportFactory;
    @Mock
    private DownloadTrigger downloadTrigger;
    @Mock
    private Export export;
    @Mock
    private Shareholder shareholder;
    @Mock
    private ShareholdersService shareholdersService;

    private ShareholderPicker shareholderPicker;
    private YearPicker yearPicker;
    private final Locale locale = Locale.ENGLISH;
    private static final int TEST_YEAR = LocalDate.now().getYear();

    @BeforeEach
    void setUp() {
        MockVaadin.setup();

        when(messageSource.getMessage(any(String.class), any(), any(Locale.class)))
                .thenAnswer(i -> i.getArgument(0));

        // Use the simple public constructors that don't require Spring services
        shareholderPicker = new ShareholderPicker(
                "Shareholder",
                () -> List.of(shareholder),
                mock(QueryParameter.class),
                "Placeholder"
        );
        yearPicker = new YearPicker("Year", List.of(TEST_YEAR), mock(QueryParameter.class));
    }

    @AfterEach
    void tearDown() {
        MockVaadin.tearDown();
    }

    // --- Construction ---

    @Test
    void constructor_createsButton_withPrintIcon() {
        final PrintButton button = createButton(Set.of());

        assertNotNull(button);
    }

    @Test
    void constructor_setsTooltip() {
        final PrintButton button = createButton(Set.of());

        assertNotNull(button.getTooltip());
    }

    // --- Click behaviour ---

    @Test
    void click_readsShareholderFromPicker() {
        when(shareholder.getFirstName()).thenReturn("Max");
        when(shareholder.getLastName()).thenReturn("Mustermann");
        when(shareholder.getDisplayId()).thenReturn(1);
        shareholderPicker.setValue(shareholder);

        when(exportFactory.createPdfExport(any(), any(), any())).thenReturn(export);
        when(export.fileName()).thenReturn("export.pdf");
        when(export.mimeType()).thenReturn("application/pdf");

        _click(createButton(Set.of(ExportViewName.BOOKINGS)));

        final ArgumentCaptor<ExportRequest> requestCaptor = ArgumentCaptor.forClass(ExportRequest.class);
        verify(exportFactory).createPdfExport(requestCaptor.capture(), any(OutputStream.class), eq(locale));
        assertEquals(List.of(shareholder), requestCaptor.getValue().shareholders());
    }

    @Test
    void click_usesSelectedYearForDateRange() {
        when(shareholder.getFirstName()).thenReturn("Max");
        when(shareholder.getLastName()).thenReturn("Mustermann");
        when(shareholder.getDisplayId()).thenReturn(1);
        shareholderPicker.setValue(shareholder);

        when(exportFactory.createPdfExport(any(), any(), any())).thenReturn(export);
        when(export.fileName()).thenReturn("export.pdf");
        when(export.mimeType()).thenReturn("application/pdf");

        _click(createButton(Set.of(ExportViewName.BOOKINGS)));

        final ArgumentCaptor<ExportRequest> requestCaptor = ArgumentCaptor.forClass(ExportRequest.class);
        verify(exportFactory).createPdfExport(requestCaptor.capture(), any(OutputStream.class), eq(locale));

        final ExportRequest request = requestCaptor.getValue();
        assertEquals(LocalDate.of(TEST_YEAR, Month.JANUARY, 1), request.startDate());
        assertEquals(LocalDate.of(TEST_YEAR, Month.DECEMBER, 31), request.endDate());
    }

    @Test
    void click_passesViewsToExportFactory() {
        when(shareholder.getFirstName()).thenReturn("Max");
        when(shareholder.getLastName()).thenReturn("Mustermann");
        when(shareholder.getDisplayId()).thenReturn(1);
        shareholderPicker.setValue(shareholder);

        when(exportFactory.createPdfExport(any(), any(), any())).thenReturn(export);
        when(export.fileName()).thenReturn("export.pdf");
        when(export.mimeType()).thenReturn("application/pdf");

        final Set<ExportViewName> views = Set.of(ExportViewName.BOOKINGS, ExportViewName.INTEREST);
        _click(createButton(views));

        final ArgumentCaptor<ExportRequest> requestCaptor = ArgumentCaptor.forClass(ExportRequest.class);
        verify(exportFactory).createPdfExport(requestCaptor.capture(), any(OutputStream.class), eq(locale));
        final ExportRequest request = requestCaptor.getValue();
        views.forEach(view -> Assertions.assertTrue(
                request.views().contains(view),
                "ExportRequest views should contain " + view
        ));
    }

    @Test
    void click_triggersExport() {
        when(shareholder.getFirstName()).thenReturn("Max");
        when(shareholder.getLastName()).thenReturn("Mustermann");
        when(shareholder.getDisplayId()).thenReturn(1);
        shareholderPicker.setValue(shareholder);

        when(exportFactory.createPdfExport(any(), any(), any())).thenReturn(export);
        when(export.fileName()).thenReturn("export.pdf");
        when(export.mimeType()).thenReturn("application/pdf");

        _click(createButton(Set.of(ExportViewName.BOOKINGS)));

        verify(export).trigger();
    }

    @Test
    void click_triggersDownloadWithExportMetadata() {
        when(shareholder.getFirstName()).thenReturn("Max");
        when(shareholder.getLastName()).thenReturn("Mustermann");
        when(shareholder.getDisplayId()).thenReturn(1);
        shareholderPicker.setValue(shareholder);

        when(exportFactory.createPdfExport(any(), any(), any())).thenReturn(export);
        when(export.fileName()).thenReturn("my-export.pdf");
        when(export.mimeType()).thenReturn("application/pdf");

        _click(createButton(Set.of(ExportViewName.BOOKINGS)));

        verify(downloadTrigger).triggerDownload(any(byte[].class), eq("my-export.pdf"), eq("application/pdf"));
    }

    // --- Helper ---

    private PrintButton createButton(final Set<ExportViewName> views) {
        return new PrintButton(
                messageSource,
                locale,
                shareholderPicker,
                yearPicker,
                downloadTrigger,
                exportFactory,
                views
        );
    }
}
