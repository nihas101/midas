package de.nihas101.midas.api.backup;

import java.nio.file.Path;

public interface MidasFileFactory {
    MidasFile create(final Path path);
}
