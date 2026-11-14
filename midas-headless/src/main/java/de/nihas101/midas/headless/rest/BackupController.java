package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.backup.BackupService;
import de.nihas101.midas.api.backup.BackupStatusReader;
import de.nihas101.midas.core.backup.service.BackupFileNameProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/backup")
@RequiredArgsConstructor
@Tag(name = "Backup", description = "Controller for creating backups and last backup date time.")
public class BackupController {

    private final BackupService backupService;
    private final BackupStatusReader backupStatusReader;
    private final BackupFileNameProvider fileNameProvider;

    /* TODO: Fix + Test
     * java.lang.RuntimeException: File '.../midas-headless/target/classes' is not a jar
	at de.nihas101.midas.core.backup.service.ProductionMidasSource.file(ProductionMidasSource.java:23) ~[classes/:na]
	at de.nihas101.midas.core.backup.service.MidasExecutableResolver.resolveExecutable(MidasExecutableResolver.java:20) ~[classes/:na]
	at de.nihas101.midas.core.backup.service.snapshot.JarSnapshot.create(JarSnapshot.java:22) ~[classes/:na]
	at de.nihas101.midas.core.backup.service.snapshot.MidasSnapshot.lambda$create$0(MidasSnapshot.java:29) ~[classes/:na]
	at java.base/java.lang.Iterable.forEach(Iterable.java:75) ~[na:na]
	at de.nihas101.midas.core.backup.service.snapshot.MidasSnapshot.create(MidasSnapshot.java:27) ~[classes/:na]
	at de.nihas101.midas.core.backup.service.DefaultBackupService.createBackup(DefaultBackupService.java:59) ~[classes/:na]
	at de.nihas101.midas.headless.rest.BackupController.createBackup(BackupController.java:59) ~[classes/:na]
     */
    @Operation(
            summary = "Create a backup",
            description = "Creates a backup."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Backup ZIP file",
                    content = @Content(
                            mediaType = "application/zip"
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
    public void createBackup(final HttpServletResponse response) throws Exception {
        final byte[] backupZip = backupService.createBackup();
        response.setContentType("application/zip");
        final String backupFileName = fileNameProvider.getBackupFileName();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + backupFileName + "\"");
        response.getOutputStream().write(backupZip);
    }

    @Operation(
            summary = "Get the status of the last successful backup",
            description = "Returns the date of the last successful backup."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success"
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
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getBackupStatus() {
        final Optional<LocalDateTime> lastSuccess = backupStatusReader.getLastSuccessAt();
        final String lastSuccessAt = lastSuccess.map(LocalDateTime::toString).orElse("");
        return ResponseEntity.ok(Map.of("lastSuccessAt", lastSuccessAt));
    }
}
