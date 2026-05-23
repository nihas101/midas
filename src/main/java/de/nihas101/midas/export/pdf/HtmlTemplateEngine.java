package de.nihas101.midas.export.pdf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlTemplateEngine {

    private final TemplateEngine htmlTemplateEngine;

    public HtmlTemplateEngine(final SpringTemplateEngine htmlTemplateEngine) {
        this(new TemplateEngine(htmlTemplateEngine));
    }

    public String generateHtml(final PdfViewData data, final TemplateContext context) {
        String html;
        try {
            // We always process base-layout, which then inserts the specific view
            html = htmlTemplateEngine.process("base-layout", context);
        } catch (Exception e) {
            log.error("Failed to process Thymeleaf template for view: {}", data.viewName(), e);
            throw new PdfExportException("Error processing template: " + e.getMessage(), e);
        }
        return html;
    }
}