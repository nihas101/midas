package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.export.Export;
import de.nihas101.midas.api.export.ZipExport;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import de.nihas101.midas.core.export.ExportFactory;
import de.nihas101.midas.core.export.ExportRequest;
import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.export.ExportViews;
import de.nihas101.midas.core.export.ZipExportStream;
import de.nihas101.midas.headless.config.MidasConfig;
import de.nihas101.midas.headless.rest.dto.ExportApiRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
@Tag(name = "Export", description = "Controller for exporting reports")
public class ExportController {

    private final ExportFactory exportFactory;
    private final ShareholdersReader shareholdersReader;
    private final MidasConfig config;

    @Operation(
            summary = "Trigger an export",
            description = "Triggers an export of the specified views and formats."
    )
    @PostMapping
    public void export(
            @NotNull
            @RequestBody final ExportApiRequest request,

            final HttpServletResponse response,
            final Locale locale
    ) throws Exception {
        final List<Shareholder> shareholders = shareholders(request.getShareholderIds());

        final Locale loc = locale != null ? locale : Locale.of(config.getI18n().getDefaultLocale());
        final ExportRequest exportRequest = new ExportRequest(
                shareholders,
                new ExportViews(
                        request.getViews() != null && !request.getViews().isEmpty()
                                ? request.getViews()
                                : new HashSet<>(List.of(ExportViewName.values()))
                ),
                request.getFrom(),
                request.getUntil(),
                request.getFormats()
        );

        final boolean hasPdf = exportRequest.formats().contains("pdf");
        final boolean hasXlsx = exportRequest.formats().contains("xlsx");

        // TODO: We are missing the check for multiple shareholders here (and multiple years)
        // If both formats (pdf and xlsx) are requested, package them into a single zip
        final boolean multipleFormats = hasPdf && hasXlsx;
        if (multipleFormats) {
            try (
                    final ZipExport zipExport = new ZipExportStream(
                            exportRequest.startDate(),
                            exportRequest.endDate(),
                            response.getOutputStream()
                    )
            ) {
                response.setContentType(zipExport.mimeType());
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipExport.name() + "\"");
                if (hasPdf) {
                    final ByteArrayOutputStream pdfBaos = new ByteArrayOutputStream();
                    final Export pdfExport = exportFactory.createPdfExport(exportRequest, pdfBaos, loc);
                    pdfExport.trigger();
                    zipExport.putEntry(
                            pdfExport.fileName(),
                            pdfBaos.toByteArray()
                    );
                }
                if (hasXlsx) {
                    final ByteArrayOutputStream xlsxBaos = new ByteArrayOutputStream();
                    final Export xlsxExport = exportFactory.createXlsxExport(exportRequest, xlsxBaos, loc);
                    xlsxExport.trigger();
                    zipExport.putEntry(
                            xlsxExport.fileName(),
                            xlsxBaos.toByteArray()
                    );

                }
            }
        } else if (hasPdf) {
            final Export pdfExport = exportFactory.createPdfExport(exportRequest, response.getOutputStream(), loc);
            response.setContentType(pdfExport.mimeType());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfExport.fileName() + "\"");
            pdfExport.trigger();
        } else if (hasXlsx) {
            final Export xlsxExport = exportFactory.createXlsxExport(exportRequest, response.getOutputStream(), loc);
            response.setContentType(xlsxExport.mimeType());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + xlsxExport.fileName() + "\"");
            xlsxExport.trigger();
        }
    }

    private List<Shareholder> shareholders(final List<Integer> shareholderIds) {
        if (shareholderIds == null || shareholderIds.isEmpty()) {
            return shareholdersReader.shareholders()
                    .toList();
        } else {
            final Set<Integer> idSet = new HashSet<>(shareholderIds);
            final Map<Integer, Shareholder> map = shareholdersReader.shareholders()
                    .toList()
                    .stream()
                    .filter(sh -> idSet.contains(sh.getId()))
                    .collect(Collectors.toMap(Shareholder::getId, Function.identity()));
            return shareholderIds.stream()
                    .map(map::get)
                    .filter(Objects::nonNull)
                    .toList();
        }
    }
}
