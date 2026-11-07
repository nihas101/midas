package de.nihas101.midas.core.export;

import de.nihas101.midas.api.export.ZipExport;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipExportStream implements ZipExport {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final ZipOutputStream outputStream;

    public ZipExportStream(
            final LocalDate startDate,
            final LocalDate endDate,
            final OutputStream outputStream
    ) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.outputStream = new ZipOutputStream(
                Objects.requireNonNull(outputStream)
        );
    }

    @Override
    public String name() {
        return "export_%s_%s.zip".formatted(
                startDate != null ? startDate.toString() : "",
                endDate != null ? endDate.toString() : ""
        );
    }

    @Override
    public String mimeType() {
        return "application/zip";
    }

    @Override
    public void close() throws Exception {
        outputStream.close();
    }

    // TODO: Add tests
    @Override
    public void putEntry(
            final String filName,
            final byte[] byteArray
    ) throws IOException {
        final ZipEntry zipEntry = new ZipEntry(filName);
        outputStream.putNextEntry(zipEntry);
        outputStream.write(byteArray);
        outputStream.closeEntry();
    }
}
