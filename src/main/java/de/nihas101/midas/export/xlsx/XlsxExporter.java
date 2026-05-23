package de.nihas101.midas.export.xlsx;

import de.nihas101.midas.export.Export;
import de.nihas101.midas.export.ExportDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class XlsxExporter implements Export {

    private final List<ExportDataSource> dataSources;
    private final OutputStream outputStream;
    private final XlsxExportTargetFactory factory;
    private final XslxFile xslxFile;

    public XlsxExporter(
            final List<ExportDataSource> dataSources,
            final OutputStream outputStream,
            final XslxFile xslxFile
    ) {
        this(
                dataSources,
                outputStream,
                new XlsxExportTargetFactory(),
                xslxFile
        );
    }

    @Override
    public void trigger() {
        if (dataSources.isEmpty()) {
            return;
        }

        try (XlsxExportTarget exportTarget = factory.exportTarget()) {
            dataSources.forEach(dataSource -> dataSource.export(exportTarget));
            exportTarget.write(outputStream);
        } catch (Exception e) {
            log.error("Failed to generate XLSX export", e);
            throw new RuntimeException("Export failed", e);
        }
    }

    @Override
    public String fileName() {
        return xslxFile.name();
    }

    @Override
    public String mimeType() {
        return xslxFile.mimeType();
    }
}
