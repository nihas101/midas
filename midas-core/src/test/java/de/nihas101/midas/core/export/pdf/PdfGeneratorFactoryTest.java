package de.nihas101.midas.core.export.pdf;

import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.export.ExportRequest;
import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.export.ExportViews;
import de.nihas101.midas.core.shareholders.dto.DefaultShareholder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PdfGeneratorFactoryTest {

    @ParameterizedTest
    @MethodSource("pdfFilesCountInput")
    public void pdfFilesCountExportRequest(
            final int startYear,
            final int endYear,
            final int shareholdersCount,
            final int viewsCount,
            final Class<?> expectedClass
    ) {
        final ExportRequest exportRequest = new ExportRequest(
                Stream.generate(DefaultShareholder::new)
                        .map(Shareholder.class::cast)
                        .limit(shareholdersCount)
                        .toList(),
                new ExportViews(Arrays.stream(ExportViewName.values()).limit(viewsCount).toList()),
                LocalDate.of(startYear, 1, 1),
                LocalDate.of(endYear, 12, 31),
                Set.of()
        );
        final PdfGeneratorFactory pdfGeneratorFactory = new PdfGeneratorFactory(
                exportRequest,
                Mockito.mock(PdfService.class),
                Locale.ENGLISH,
                Mockito.mock(OutputStream.class),
                Mockito.mock(PdfViewDataExtractor.class)
        );
        assertEquals(expectedClass, pdfGeneratorFactory.createPdfGenerator().getClass());
    }

    public static Stream<Arguments> pdfFilesCountInput() {
        return Stream.of(
                Arguments.of(2026, 2026, 1, 1, SinglePdfGenerator.class),
                Arguments.of(2026, 2026, 8, 1, MultiPdfGenerator.class),
                Arguments.of(2026, 2026, 1, ExportViewName.values().length, MultiPdfGenerator.class),
                Arguments.of(2024, 2026, 1, 1, MultiPdfGenerator.class),
                Arguments.of(2024, 2026, 8, ExportViewName.values().length, MultiPdfGenerator.class)
        );
    }
}