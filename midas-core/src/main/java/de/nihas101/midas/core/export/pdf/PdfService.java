package de.nihas101.midas.core.export.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfService {

    private final HtmlTemplateEngine htmlTemplateEngine;
    private final FontRegister fontRegister;
    private final HeaderImageResolver headerImage;

    public void generatePdf(
            final PdfViewData data,
            final Locale locale,
            final OutputStream outputStream
    ) {
        final TemplateContext context = new TemplateContext(locale);
        context.setVariable("data", data);
        context.setVariable("content", data.viewName().getName()); // This will be used in base-layout.html
        context.setVariable("headerImage", headerImage.dataUri());

        try {
            final String html = htmlTemplateEngine.generateHtml(data, context);
            final PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.useSVGDrawer(new BatikSVGDrawer());
            builder.withHtmlContent(html, null);

            fontRegister.registerLiberationSerifFonts(builder);

            builder.toStream(outputStream);
            builder.run();
        } catch (Exception e) {
            log.error("Failed to generate PDF for view: {}", data.viewName(), e);
            throw new PdfExportException("Error generating PDF: " + e.getMessage(), e);
        }
    }
}
