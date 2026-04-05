package de.nihas101.midas.backup.service.snapshot;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public interface ArchiveWriter {

    void add(File file) throws IOException;

    void add(File file, String name) throws IOException;

    void add(Properties props) throws IOException;
}
