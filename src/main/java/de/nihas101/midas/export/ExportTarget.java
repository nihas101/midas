package de.nihas101.midas.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface ExportTarget {
    void write(final OutputStream outputStream) throws IOException;

    void export(
            final String sheetName,
            final List<String> headers,
            final List<List<Object>> rows
    );
}
