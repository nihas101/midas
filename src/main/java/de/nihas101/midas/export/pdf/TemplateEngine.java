package de.nihas101.midas.export.pdf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateEngine {

    private final SpringTemplateEngine springTemplateEngine;

    public String process(final String template, final TemplateContext templateContext) {
        return springTemplateEngine.process(
                template,
                new Context(
                        templateContext.locale(),
                        templateContext.variables()
                )
        );
    }
}
