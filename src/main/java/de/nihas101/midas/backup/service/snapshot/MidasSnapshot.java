package de.nihas101.midas.backup.service.snapshot;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
public class MidasSnapshot implements Snapshot {

    private final Collection<Snapshot> snapshots;

    @Override
    public void create() throws IOException {
        snapshots.forEach(snapshot -> {
            try {
                snapshot.create();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void close() throws Exception {
        snapshots.forEach(snapshot -> {
            try {
                snapshot.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
