package de.nihas101.midas.backup.service.snapshot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CleanupSnapshotFileTest {

    @TempDir
    Path tempDir;

    @Test
    void cleanup() throws IOException {
        Path testFile = tempDir.resolve("test1.txt");
        Assertions.assertTrue(testFile.toFile().createNewFile());
        final CleanupSnapshotFile cleanupSnapshotFile = new CleanupSnapshotFile(testFile.toAbsolutePath().toString());
        cleanupSnapshotFile.cleanup();
        Assertions.assertFalse(testFile.toFile().exists());
    }

    @Test
    void cleanupFileDoesNotExist() {
        Path testFile = tempDir.resolve("test2.txt");
        Assertions.assertFalse(testFile.toFile().exists());
        final CleanupSnapshotFile cleanupSnapshotFile = new CleanupSnapshotFile(testFile.toAbsolutePath().toString());
        cleanupSnapshotFile.cleanup();
        Assertions.assertFalse(testFile.toFile().exists());
        // No exception occurs
    }
}