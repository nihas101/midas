package de.nihas101.midas.core.backup.service;

import de.nihas101.midas.persistance.backup.ArchiveWriter;
import de.nihas101.midas.persistance.backup.Snapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class HeaderImageSnapshot implements Snapshot {

    private final ArchiveWriter archiveWriter;
    private final MidasHeaderImageResolver headerImageResolver;

    @Override
    public void create() throws IOException {
        final Optional<File> headerImage = headerImageResolver.resolveHeaderImage();
        if (headerImage.isEmpty()) {
            log.debug("No header image defined or found. Skipping header image backup.");
            return;
        }

        final File imageFile = headerImage.get();
        log.info("Adding header image {} to zip...", imageFile.getName());
        archiveWriter.add(imageFile, imageFile.getName());
    }

    @Override
    public void close() throws Exception {
        // Nothing to do
    }
}
