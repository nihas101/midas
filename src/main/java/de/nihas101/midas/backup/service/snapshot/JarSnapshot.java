package de.nihas101.midas.backup.service.snapshot;

import de.nihas101.midas.backup.service.MidasExecutableResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
public class JarSnapshot implements Snapshot {

    private final ZipOutputStream zos; // TODO: Abstract away
    private final MidasExecutableResolver executableResolver;

    @Override
    public void create() throws IOException {
        final Optional<File> jarFile = executableResolver.resolveExecutable();
        if (jarFile.isPresent()) {
            final File file = jarFile.get();
            log.info("Adding JAR file {} to zip...", file.getName());
            zos.putNextEntry(new ZipEntry(file.getName()));
            Files.copy(file.toPath(), zos);
            zos.closeEntry();
        } else {
            log.warn("Application is not running from a JAR file. Skipping JAR backup.");
        }
    }

    @Override
    public void close() throws Exception {
        // Nothing to do
    }
}
