package de.nihas101.midas.backup.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

class BackupFileNameProviderTest {

    private final MidasExecutableNameResolver midasExecutableNameResolver = Mockito.mock(MidasExecutableNameResolver.class);
    private final BackupFileNameProvider backupFileNameProvider = new BackupFileNameProvider(
            midasExecutableNameResolver,
            () -> LocalDateTime.of(2026, 4, 4, 12, 0, 13)
    );

    @Test
    void getBackupFileName() {
        final String executableName = "midas";
        Mockito.when(midasExecutableNameResolver.getExecutableName()).thenReturn(executableName);

        final String fileName = backupFileNameProvider.getBackupFileName();

        Assertions.assertEquals("midas_backup_20260404_120013.zip", fileName);
    }
}
