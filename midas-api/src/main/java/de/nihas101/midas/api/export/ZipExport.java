package de.nihas101.midas.api.export;

import java.io.IOException;

public interface ZipExport extends AutoCloseable {
    String name();

    String mimeType();

    void putEntry(
            String filName,
            byte[] byteArray
    ) throws IOException;
}
