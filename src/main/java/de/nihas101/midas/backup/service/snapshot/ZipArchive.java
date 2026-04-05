package de.nihas101.midas.backup.service.snapshot;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
public class ZipArchive implements ArchiveWriter, AutoCloseable {

    private final ByteArrayOutputStream baos;
    private final ZipOutputStream zos;

    public void add(File file) throws IOException {
        add(file, file.getName());
    }

    @Override
    public void add(final File file, final String name) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        Files.copy(file.toPath(), zos);
        zos.closeEntry();
    }

    @Override
    public void add(final Properties props) throws IOException {
        zos.putNextEntry(new ZipEntry("application.properties"));
        props.store(zos, "Application Properties");
        zos.closeEntry();
    }

    @Override
    public void close() throws Exception {
        baos.close();
        zos.close();
    }

    public byte[] byteArray() throws IOException {
        zos.finish();
        return baos.toByteArray();
    }
}
