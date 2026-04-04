package de.nihas101.midas.backup.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.regex.Pattern;

class BackupFileNameProviderTest {

    private final MidasExecutableResolver executableResolver = Mockito.mock(MidasExecutableResolver.class);
    private final BackupFileNameProvider backupFileNameProvider = new BackupFileNameProvider(executableResolver);

    @Test
    void getBackupFileName() {
        // Arrange
        final String executableName = "my-midas-app";
        Mockito.when(executableResolver.getExecutableName()).thenReturn(executableName);

        // Act
        final String fileName = backupFileNameProvider.getBackupFileName();

        // Assert
        // Pattern: baseName_backup_yyyyMMdd_HHmmss.zip
        final String regex = "^" + Pattern.quote(executableName) + "_backup_\\d{8}_\\d{6}\\.zip$";
        Assertions.assertTrue(fileName.matches(regex), 
                "Filename '" + fileName + "' does not match pattern: " + regex);
    }

    @Test
    void getBackupFileNameWithDefault() {
        // Arrange
        Mockito.when(executableResolver.getExecutableName()).thenReturn("midas");

        // Act
        final String fileName = backupFileNameProvider.getBackupFileName();

        // Assert
        final String regex = "^midas_backup_\\d{8}_\\d{6}\\.zip$";
        Assertions.assertTrue(fileName.matches(regex));
    }
}
