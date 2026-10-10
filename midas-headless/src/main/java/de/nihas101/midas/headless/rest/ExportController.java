package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.export.Export;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.core.export.ExportFactory;
import de.nihas101.midas.core.export.ExportRequest;
import de.nihas101.midas.core.export.ExportViews;
import de.nihas101.midas.core.shareholders.service.ShareholdersService;
import de.nihas101.midas.headless.rest.dto.ExportApiRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.Collections.emptySet;

@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportFactory exportFactory;
    private final ShareholdersService shareholdersService;

    @PostMapping
    public void export(
            @RequestBody final ExportApiRequest request,
            final Locale locale,
            final HttpServletResponse response
    ) throws Exception {
        if (request.getFrom() == null || request.getUntil() == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Date range ('from' and 'until') is required");
            return;
        }

        if (request.getUntil().isBefore(request.getFrom())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'until' cannot be before 'from'");
            return;
        }

        if (request.getFormats() == null || request.getFormats().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "At least one format must be selected");
            return;
        }

        final List<Shareholder> shareholders = (request.getShareholderIds() == null || request.getShareholderIds().isEmpty())
                ? shareholdersService.shareholders().toList()
                : request.getShareholderIds().stream()
                .map(shareholdersService::shareholder)
                .filter(s -> s != null)
                .toList();

        final Locale loc = locale != null ? locale : LocaleContextHolder.getLocale();
        final ExportRequest exportRequest = new ExportRequest(
                shareholders,
                new ExportViews(request.getViews() != null ? request.getViews() : emptySet()),
                request.getFrom(),
                request.getUntil(),
                request.getFormats()
        );

        final boolean hasPdf = request.getFormats().contains("pdf");
        final boolean hasXlsx = request.getFormats().contains("xlsx");

        // If both formats or multi-year PDF, package into zip
        final boolean isMultiYear = exportRequest.startDate().getYear() != exportRequest.endDate().getYear();
        final boolean multipleFormats = hasPdf && hasXlsx;

        if (multipleFormats) {
            response.setContentType("application/zip");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"export.zip\"");
            try (final ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                if (hasPdf) {
                    final ByteArrayOutputStream pdfBaos = new ByteArrayOutputStream();
                    final Export pdfExport = exportFactory.createPdfExport(exportRequest, pdfBaos, loc);
                    pdfExport.trigger();
                    zos.putNextEntry(new ZipEntry(pdfExport.fileName()));
                    zos.write(pdfBaos.toByteArray());
                    zos.closeEntry();
                }
                if (hasXlsx) {
                    final ByteArrayOutputStream xlsxBaos = new ByteArrayOutputStream();
                    final Export xlsxExport = exportFactory.createXlsxExport(exportRequest, xlsxBaos, loc);
                    xlsxExport.trigger();
                    zos.putNextEntry(new ZipEntry(xlsxExport.fileName()));
                    zos.write(xlsxBaos.toByteArray());
                    zos.closeEntry();
                }
            }
            return;
        }

        if (hasPdf) {
            final Export pdfExport = exportFactory.createPdfExport(exportRequest, response.getOutputStream(), loc);
            response.setContentType(pdfExport.mimeType());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfExport.fileName() + "\"");
            pdfExport.trigger();
            return;
        }

        if (hasXlsx) {
            final Export xlsxExport = exportFactory.createXlsxExport(exportRequest, response.getOutputStream(), loc);
            response.setContentType(xlsxExport.mimeType());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + xlsxExport.fileName() + "\"");
            xlsxExport.trigger();
        }
    }
}
