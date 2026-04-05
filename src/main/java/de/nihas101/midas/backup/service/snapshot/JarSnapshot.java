package de.nihas101.midas.backup.service.snapshot;

import de.nihas101.midas.backup.service.MidasExecutableResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JarSnapshot implements Snapshot {

    private final ArchiveWriter archiveWriter;
    private final MidasExecutableResolver executableResolver;

    @Override
    public void create() throws IOException {
        final Optional<File> jarFile = executableResolver.resolveExecutable();
        if (jarFile.isPresent()) {
            final File file = jarFile.get();
            log.info("Adding JAR file {} to zip...", file.getName());
            archiveWriter.add(file);
        } else {
            log.warn("Application is not running from a JAR file. Skipping JAR backup.");
        }
    }

    @Override
    public void close() throws Exception {
        // Nothing to do
    }
}
