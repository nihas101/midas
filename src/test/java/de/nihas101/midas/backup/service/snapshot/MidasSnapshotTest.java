package de.nihas101.midas.backup.service.snapshot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MidasSnapshotTest {

    @Test
    void createDelegatesToAllSnapshots() throws IOException {
        Snapshot s1 = mock(Snapshot.class);
        Snapshot s2 = mock(Snapshot.class);
        MidasSnapshot composite = new MidasSnapshot(s1, s2);

        composite.create();

        verify(s1).create();
        verify(s2).create();
    }

    @Test
    void createWithNullDelegate() throws IOException {
        Snapshot s1 = mock(Snapshot.class);
        MidasSnapshot composite = new MidasSnapshot(s1, null);

        composite.create();

        verify(s1).create();
        // No NPE caused
    }

    @Test
    void createWrapsIOExceptionInRuntimeException() throws IOException {
        Snapshot s1 = mock(Snapshot.class);
        doThrow(new IOException("Disk full")).when(s1).create();
        MidasSnapshot composite = new MidasSnapshot(s1);

        Assertions.assertThrows(RuntimeException.class, composite::create);
    }

    @Test
    void closeDelegatesToAllSnapshots() throws Exception {
        Snapshot s1 = mock(Snapshot.class);
        Snapshot s2 = mock(Snapshot.class);
        MidasSnapshot composite = new MidasSnapshot(s1, s2);

        composite.close();

        verify(s1).close();
        verify(s2).close();
    }

    @Test
    void closeWithNullDelegate() throws Exception {
        Snapshot s1 = mock(Snapshot.class);
        MidasSnapshot composite = new MidasSnapshot(s1, null);

        composite.close();

        verify(s1).close();
        // No NPE caused
    }

    @Test
    void closeWrapsExceptionInRuntimeException() throws Exception {
        Snapshot s1 = mock(Snapshot.class);
        doThrow(new Exception("Closure failed")).when(s1).close();
        MidasSnapshot composite = new MidasSnapshot(s1);

        Assertions.assertThrows(RuntimeException.class, composite::close);
    }
}
