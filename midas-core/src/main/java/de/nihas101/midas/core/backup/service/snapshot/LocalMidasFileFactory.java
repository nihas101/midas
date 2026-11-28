package de.nihas101.midas.core.backup.service.snapshot;

import de.nihas101.midas.api.backup.MidasFile;
import de.nihas101.midas.api.backup.MidasFileFactory;

import java.nio.file.Path;

public class LocalMidasFileFactory implements MidasFileFactory {

    @Override
    public MidasFile create(final Path path) {
        return new LocalMidasFile(path);
    }
}
