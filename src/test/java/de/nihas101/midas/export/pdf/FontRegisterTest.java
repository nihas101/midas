package de.nihas101.midas.export.pdf;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FontRegisterTest {

    @Mock
    PdfRendererBuilder builder;

    @InjectMocks
    FontRegister fontRegister;

    @Test
    void registerLiberationSerifFonts_callsBuilderFourTimes() throws IOException {
        when(builder.useFont(
                any(FSSupplier.class),
                anyString(),
                anyInt(),
                any(),
                anyBoolean())
        ).thenReturn(builder);

        fontRegister.registerLiberationSerifFonts(builder);

        for (final Integer fontWeight : List.of(400, 700)) {
            for (final FontStyle fontStyle : List.of(FontStyle.NORMAL, FontStyle.ITALIC)) {
                verify(builder).useFont(
                        any(FSSupplier.class),
                        eq("Liberation Serif"),
                        eq(fontWeight),
                        eq(fontStyle),
                        eq(true)
                );
            }
        }
        verify(builder, times(4)).useFont(
                any(FSSupplier.class),
                anyString(),
                anyInt(),
                any(),
                anyBoolean()
        );
    }
}