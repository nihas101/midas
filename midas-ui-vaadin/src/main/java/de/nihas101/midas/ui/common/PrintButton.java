package de.nihas101.midas.ui.common;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.nihas101.midas.core.export.Export;
import de.nihas101.midas.core.export.ExportFactory;
import de.nihas101.midas.core.export.ExportRequest;
import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.export.ExportViews;
import de.nihas101.midas.core.shareholders.dto.Shareholder;
import org.springframework.context.MessageSource;

import java.io.ByteArrayOutputStream;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PrintButton extends Button { // TODO: test

    public PrintButton(
            final MessageSource messageSource,
            final Locale locale,
            final ShareholderPicker shareholderPicker,
            final YearPicker yearPicker,
            final DownloadTrigger downloadTrigger,
            final ExportFactory exportFactory,
            final Set<ExportViewName> viewsToExport
    ) {
        super(
                VaadinIcon.PRINT.create(),
                e -> {
                    final Shareholder shareholder = shareholderPicker.getValue();
                    final Year year = Year.of(yearPicker.getValue());

                    final ExportRequest request = new ExportRequest(
                            List.of(shareholder),
                            new ExportViews(viewsToExport, messageSource, locale),
                            year.atMonth(Month.JANUARY).atDay(1),
                            year.atMonth(Month.DECEMBER).atEndOfMonth(),
                            Set.of("pdf")
                    );

                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    final Export pdfExport = exportFactory.createPdfExport(request, out, locale);
                    pdfExport.trigger();
                    final byte[] data = out.toByteArray();
                    downloadTrigger.triggerDownload(data, pdfExport.fileName(), pdfExport.mimeType());
                }
        );
        setTooltipText(messageSource.getMessage("printbutton.tooltip", null, locale));
    }
}
