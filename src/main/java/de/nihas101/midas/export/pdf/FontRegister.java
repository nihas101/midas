package de.nihas101.midas.export.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class FontRegister {

    public void registerLiberationSerifFonts(final PdfRendererBuilder builder) throws IOException {
        registerFont(builder, "fonts/LiberationSerif-Regular.ttf", "Liberation Serif", 400, false);
        registerFont(builder, "fonts/LiberationSerif-Bold.ttf", "Liberation Serif", 700, false);
        registerFont(builder, "fonts/LiberationSerif-Italic.ttf", "Liberation Serif", 400, true);
        registerFont(builder, "fonts/LiberationSerif-BoldItalic.ttf", "Liberation Serif", 700, true);
    }

    private void registerFont(
            final PdfRendererBuilder builder,
            final String path,
            final String family,
            final int weight,
            final boolean italic
    ) throws IOException {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            builder.useFont(
                    () -> is,
                    family,
                    weight,
                    italic ? PdfRendererBuilder.FontStyle.ITALIC : PdfRendererBuilder.FontStyle.NORMAL,
                    true
            );
        }
    }
}