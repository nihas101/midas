package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.export.Export;
import de.nihas101.midas.api.export.ExportFormat;
import de.nihas101.midas.api.shareholder.Shareholder;
import de.nihas101.midas.api.shareholder.Shareholders;
import de.nihas101.midas.api.shareholder.ShareholdersReader;
import de.nihas101.midas.core.export.ExportFactory;
import de.nihas101.midas.core.export.ExportRequest;
import de.nihas101.midas.core.export.ExportViewName;
import de.nihas101.midas.core.export.ExportViews;
import de.nihas101.midas.headless.config.MidasConfig;
import de.nihas101.midas.headless.rest.dto.RequestExport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
@Tag(name = "Export", description = "Controller for exporting reports")
public class ExportController {

    private static final Logger log = LoggerFactory.getLogger(ExportController.class);
    private final ExportFactory exportFactory;
    private final ShareholdersReader shareholdersReader;
    private final MidasConfig config;

    @Operation(
            summary = "Trigger an export",
            description = "Triggers an export of the specified views in PDF or XLSX format. "
                    + "Returns application/pdf (single PDF), application/vnd.openxmlformats-officedocument.spreadsheetml.sheet (Excel), "
                    + "or application/zip when multiple PDFs (e.g. multiple shareholders or years) are generated."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Export file",
                    content = {
                            @Content(mediaType = "application/pdf"),
                            @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                            @Content(mediaType = "application/zip")
                    }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            schema = @Schema(
                                    example = "{\"error\":\"Invalid request parameter\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            schema = @Schema(
                                    example = "{\"error\":\"Internal Server Error\"}"
                            )
                    )
            )
    })
    @PostMapping
    public void export(
            @NotNull
            @RequestBody final RequestExport request,

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
                request.getFormat()
        );

        final Set<ExportFormat> formats = exportRequest.formats();
        if (formats.contains(ExportFormat.PDF)) {
            final Export pdfExport = exportFactory.createPdfExport(exportRequest, response.getOutputStream(), loc);
            response.setContentType(pdfExport.mimeType());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfExport.fileName() + "\"");
            pdfExport.trigger();
        } else if (formats.contains(ExportFormat.XLSX)) {
            final Export xlsxExport = exportFactory.createXlsxExport(exportRequest, response.getOutputStream(), loc);
            response.setContentType(xlsxExport.mimeType());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + xlsxExport.fileName() + "\"");
            xlsxExport.trigger();
        }
    }

    private List<Shareholder> shareholders(final List<Integer> shareholderIds) {
        final Shareholders shareholders = shareholderIds == null || shareholderIds.isEmpty()
                ? shareholdersReader.shareholders()
                : shareholdersReader.shareholders(shareholderIds);
        return shareholders.toList();
    }
}
