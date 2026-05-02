package de.nihas101.midas.export.pdf;

import de.nihas101.midas.export.ExportTarget;
import lombok.Getter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PdfExportTarget implements ExportTarget {

    private final List<ViewData> views = new ArrayList<>();

    @Override
    public void write(OutputStream outputStream) throws IOException {
        // Not used directly here
    }

    @Override
    public void export(String viewName, List<String> headers, List<List<Object>> rows) {
        views.add(new ViewData(viewName, headers, rows));
    }

    public record ViewData(String name, List<String> headers, List<List<Object>> rows) {}
}
