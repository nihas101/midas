package de.nihas101.midas.core.backup.service.snapshot;

import de.nihas101.midas.api.backup.MidasFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocalMidasFile implements MidasFile {

    private final Path path;

    public LocalMidasFile(final Path path) {
        this.path = path;
    }

    @Override
    public boolean exists() {
        return Files.exists(path);
    }

    @Override
    public InputStream inputStream() throws IOException {
        return Files.newInputStream(path);
    }
}
