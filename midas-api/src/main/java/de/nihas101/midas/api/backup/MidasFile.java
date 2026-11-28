package de.nihas101.midas.api.backup;

import java.io.IOException;
import java.io.InputStream;

public interface MidasFile {
    boolean exists();

    InputStream inputStream() throws IOException;
}
