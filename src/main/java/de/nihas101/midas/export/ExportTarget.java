package de.nihas101.midas.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface ExportTarget {
    void write(OutputStream outputStream) throws IOException;

    void export(String sheetName, List<String> headers, List<List<Object>> rows);
}
