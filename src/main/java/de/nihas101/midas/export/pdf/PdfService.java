package de.nihas101.midas.export.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfService {

    private final SpringTemplateEngine pdfTemplateEngine;

    public void generatePdf(PdfViewData data, Locale locale, OutputStream outputStream) {
        Context context = new Context(locale);
        context.setVariable("data", data);
        context.setVariable("content", data.viewName()); // This will be used in base-layout.html

        String html;
        try {
            // We always process base-layout, which then inserts the specific view
            html = pdfTemplateEngine.process("base-layout", context);
        } catch (Exception e) {
            log.error("Failed to process Thymeleaf template for view: {}", data.viewName(), e);
            throw new PdfExportException("Error processing template: " + e.getMessage(), e);
        }

        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);

            // Register Liberation Serif fonts
            registerFonts(builder);

            builder.toStream(outputStream);
            builder.run();
        } catch (Exception e) {
            log.error("Failed to generate PDF for view: {}", data.viewName(), e);
            throw new PdfExportException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    private void registerFonts(PdfRendererBuilder builder) throws IOException {
        registerFont(builder, "fonts/LiberationSerif-Regular.ttf", "Liberation Serif", 400, false);
        registerFont(builder, "fonts/LiberationSerif-Bold.ttf", "Liberation Serif", 700, false);
        registerFont(builder, "fonts/LiberationSerif-Italic.ttf", "Liberation Serif", 400, true);
        registerFont(builder, "fonts/LiberationSerif-BoldItalic.ttf", "Liberation Serif", 700, true);
    }

    private void registerFont(PdfRendererBuilder builder, String path, String family, int weight, boolean italic) throws IOException {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            builder.useFont(() -> is, family, weight, italic ? PdfRendererBuilder.FontStyle.ITALIC : PdfRendererBuilder.FontStyle.NORMAL, true);
        }
    }
}
