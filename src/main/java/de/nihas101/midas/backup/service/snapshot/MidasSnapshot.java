package de.nihas101.midas.backup.service.snapshot;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MidasSnapshot implements Snapshot {

    private final Collection<Snapshot> snapshots;

    public MidasSnapshot(final Snapshot... snapshots) {
        this(
                Arrays.stream(snapshots)
                        .filter(Objects::nonNull)
                        .toList()
        );
    }

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
