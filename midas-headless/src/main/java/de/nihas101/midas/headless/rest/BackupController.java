package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.backup.BackupService;
import de.nihas101.midas.api.backup.BackupStatusReader;
import de.nihas101.midas.core.backup.service.BackupFileNameProvider;
import io.swagger.v3.oas.annotations.Operation;
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

    /* TODO: Investigate the following
     * {
     *   "message": "File '/home/deck/code/java/midas/midas-headless/target/classes' is not a jar",
     *   "error": "Internal Server Error"
     * }
     */
    @Operation(
            summary = "Create a backup",
            description = "Creates a backup."
    )
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
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getBackupStatus() {
        final Optional<LocalDateTime> lastSuccess = backupStatusReader.getLastSuccessAt();
        final String lastSuccessAt = lastSuccess.map(LocalDateTime::toString).orElse("");
        return ResponseEntity.ok(Map.of("lastSuccessAt", lastSuccessAt));
    }
}
