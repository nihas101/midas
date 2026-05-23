package de.nihas101.midas.export.pdf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Component
public class HtmlTemplateEngine {

    private final TemplateEngine templateEngine;

    public HtmlTemplateEngine(final SpringTemplateEngine templateEngine) {
        this(new TemplateEngine(templateEngine));
    }

    @Autowired
    public HtmlTemplateEngine(final TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String generateHtml(final PdfViewData data, final TemplateContext context) {
        String html;
        try {
            // We always process base-layout, which then inserts the specific view
            html = templateEngine.process("base-layout", context);
        } catch (Exception e) {
            log.error("Failed to process Thymeleaf template for view: {}", data.viewName(), e);
            throw new PdfExportException("Error processing template: " + e.getMessage(), e);
        }
        return html;
    }
}