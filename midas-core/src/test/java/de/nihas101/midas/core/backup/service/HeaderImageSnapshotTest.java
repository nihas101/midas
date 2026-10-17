package de.nihas101.midas.core.backup.service;

import de.nihas101.midas.persistance.backup.ArchiveWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HeaderImageSnapshotTest {

    private ArchiveWriter archiveWriter;
    private MidasHeaderImageResolver headerImageResolver;
    private HeaderImageSnapshot headerImageSnapshot;

    @BeforeEach
    void setUp() {
        archiveWriter = mock(ArchiveWriter.class);
        headerImageResolver = mock(MidasHeaderImageResolver.class);
        headerImageSnapshot = new HeaderImageSnapshot(archiveWriter, headerImageResolver);
    }

    @Test
    void createWithNoHeaderImageDoesNothing() throws IOException {
        when(headerImageResolver.resolveHeaderImage()).thenReturn(Optional.empty());

        headerImageSnapshot.create();

        verify(archiveWriter, never()).add(ArgumentMatchers.any(File.class), ArgumentMatchers.anyString());
    }

    @Test
    void createWithHeaderImageAddsToArchive() throws IOException {
        final File file = mock(File.class);
        when(file.getName()).thenReturn("logo.png");
        when(headerImageResolver.resolveHeaderImage()).thenReturn(Optional.of(file));

        headerImageSnapshot.create();

        verify(archiveWriter).add(file, "logo.png");
    }
}
