package de.nihas101.midas.backup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class BackupFileNameProvider {

    private final MidasExecutableResolver executableResolver;
    private final DateTimeFormatter dateTimeFormatter;
    private final String fileFormat;

    public BackupFileNameProvider(final MidasExecutableResolver executableResolver) {
        this(
                executableResolver,
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"),
                "%s_backup_%s"
        );
    }

    public String getBackupFileName() {
        final String baseName = executableResolver.getExecutableName();
        final String timestamp = LocalDateTime.now().format(dateTimeFormatter);
        // TODO: Need to decide the extension based on the backup
        return String.format(fileFormat + ".zip", baseName, timestamp);
    }
}
