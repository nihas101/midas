package de.nihas101.midas.export.pdf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HtmlTemplateEngineTest {

    @Mock
    private TemplateEngine templateEngine;

    @Test
    void generateHtml_successfulReturnsHtml() {
        // Arrange
        final TemplateContext context = new TemplateContext(Locale.ENGLISH);
        final PdfViewData data = mock(PdfViewData.class);
        when(templateEngine.process(eq("base-layout"), any(TemplateContext.class)))
                .thenReturn("<html>generated</html>");

        // Act
        final String result = new HtmlTemplateEngine(templateEngine).generateHtml(data, context);

        // Assert
        assertEquals("<html>generated</html>", result);
        verify(templateEngine).process(eq("base-layout"), eq(context));
    }

    @Test
    void generateHtml_processingFailureThrowsPdfExportException() {
        // Arrange
        final TemplateContext context = new TemplateContext(Locale.ENGLISH);
        final PdfViewData data = mock(PdfViewData.class);
        when(data.viewName()).thenReturn("test-view");
        when(templateEngine.process(eq("base-layout"), eq(context)))
                .thenThrow(new RuntimeException("template error"));

        // Act & Assert
        final PdfExportException ex = assertThrows(
                PdfExportException.class,
                () -> new HtmlTemplateEngine(templateEngine).generateHtml(data, context)
        );
        assertTrue(ex.getMessage().contains("Error processing template"));
        verify(templateEngine).process(eq("base-layout"), eq(context));
    }
}