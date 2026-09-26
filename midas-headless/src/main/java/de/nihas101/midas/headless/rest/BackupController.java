package de.nihas101.midas.headless.rest;

import de.nihas101.midas.api.backup.BackupService;
import de.nihas101.midas.api.backup.BackupStatusReader;
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
public class BackupController {

    private final BackupService backupService;
    private final BackupStatusReader backupStatusReader;

    @PostMapping
    public void createBackup(final HttpServletResponse response) throws Exception {
        final byte[] backupZip = backupService.createBackup();
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"midas-backup.zip\"");
        response.getOutputStream().write(backupZip);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getBackupStatus() {
        final Optional<LocalDateTime> lastSuccess = backupStatusReader.getLastSuccessAt();
        final String succ = lastSuccess.map(LocalDateTime::toString).orElse("never");
        return ResponseEntity.ok(Map.of("lastSuccessAt", succ));
    }
}
