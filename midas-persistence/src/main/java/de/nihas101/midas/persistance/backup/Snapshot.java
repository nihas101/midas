package de.nihas101.midas.persistance.backup;

import java.io.IOException;

public interface Snapshot extends AutoCloseable {
    void create() throws IOException; // TODO: Rename to persist?
}
