package de.nihas101.midas.backup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class BackupFileNameProvider {

    private final MidasExecutableNameResolver midasExecutableNameResolver;
    private final DateTimeFormatter dateTimeFormatter;
    private final String fileNameFormat;
    private final Supplier<LocalDateTime> localDateTimeSupplier;

    @Autowired
    public BackupFileNameProvider(final MidasExecutableNameResolver midasExecutableNameResolver) {
        this(
                midasExecutableNameResolver,
                LocalDateTime::now
        );
    }

    public BackupFileNameProvider(
            final MidasExecutableNameResolver midasExecutableNameResolver,
            Supplier<LocalDateTime> localDateTimeSupplier
    ) {
        this(
                midasExecutableNameResolver,
                DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"),
                "%s_backup_%s.zip",
                localDateTimeSupplier
        );
    }

    public String getBackupFileName() {
        final String baseName = midasExecutableNameResolver.getExecutableName();
        final String timestamp = localDateTimeSupplier.get().format(dateTimeFormatter);
        return String.format(fileNameFormat, baseName, timestamp);
    }
}
