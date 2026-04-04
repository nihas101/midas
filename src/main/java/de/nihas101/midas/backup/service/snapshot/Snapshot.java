package de.nihas101.midas.backup.service.snapshot;

import java.io.IOException;

public interface Snapshot extends AutoCloseable {
    void create() throws IOException;
}
