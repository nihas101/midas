package de.nihas101.midas.export.pdf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class PdfExportConfig {

    @Value("${midas.export.pdf.template-path:#{null}}")
    private String externalTemplatePath;

    @Bean
    public SpringTemplateEngine pdfTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        // External template resolver (optional, highest priority)
        if (externalTemplatePath != null && !externalTemplatePath.isEmpty()) {
            templateEngine.addTemplateResolver(fileTemplateResolver());
        }

        // Default classpath template resolver
        templateEngine.addTemplateResolver(classpathTemplateResolver());

        return templateEngine;
    }

    private ITemplateResolver classpathTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/export/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(2);
        resolver.setCheckExistence(true);
        return resolver;
    }

    private ITemplateResolver fileTemplateResolver() {
        FileTemplateResolver resolver = new FileTemplateResolver();
        String prefix = externalTemplatePath.endsWith("/") ? externalTemplatePath : externalTemplatePath + "/";
        resolver.setPrefix(prefix);
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(1);
        resolver.setCheckExistence(true);
        return resolver;
    }
}
