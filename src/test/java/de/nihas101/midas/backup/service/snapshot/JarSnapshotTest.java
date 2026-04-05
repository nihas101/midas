package de.nihas101.midas.backup.service.snapshot;

import de.nihas101.midas.backup.service.MidasExecutableResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JarSnapshotTest {

    private ArchiveWriter archiveWriter;
    private MidasExecutableResolver executableResolver;
    private JarSnapshot jarSnapshot;

    @BeforeEach
    void setUp() {
        archiveWriter = mock(ArchiveWriter.class);
        executableResolver = mock(MidasExecutableResolver.class);
        jarSnapshot = new JarSnapshot(archiveWriter, executableResolver);
    }

    @Test
    void createWithJarPresent() throws IOException {
        final File jarFile = new File("midas.jar");
        when(executableResolver.resolveExecutable()).thenReturn(Optional.of(jarFile));

        jarSnapshot.create();

        verify(archiveWriter).add(jarFile);
    }

    @Test
    void createWithJarMissing() throws IOException {
        when(executableResolver.resolveExecutable()).thenReturn(Optional.empty());

        jarSnapshot.create();

        verify(archiveWriter, never()).add(org.mockito.ArgumentMatchers.any(File.class));
    }
}
