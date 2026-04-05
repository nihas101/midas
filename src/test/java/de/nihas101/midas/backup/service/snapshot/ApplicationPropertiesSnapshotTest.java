package de.nihas101.midas.backup.service.snapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationPropertiesSnapshotTest {

    private ArchiveWriter archiveWriter;
    private DatabaseLocation databaseLocation;
    private PropertiesLoader propertiesLoader;
    private ApplicationPropertiesSnapshot snapshot;

    @BeforeEach
    void setUp() {
        archiveWriter = mock(ArchiveWriter.class);
        databaseLocation = mock(DatabaseLocation.class);
        propertiesLoader = mock(PropertiesLoader.class);
        snapshot = new ApplicationPropertiesSnapshot(archiveWriter, databaseLocation, propertiesLoader);
    }

    @Test
    void createWithNullProperties() throws IOException {
        when(propertiesLoader.load()).thenReturn(null);

        snapshot.create();

        verify(archiveWriter, never()).add((Properties) null);
        verify(archiveWriter, never()).add(new Properties());
    }

    @Test
    void createWithUrlAdjustment() throws IOException {
        Properties props = new Properties();
        props.setProperty("spring.datasource.url", "jdbc:sqlite:/absolute/path/to/midas.db");

        when(propertiesLoader.load()).thenReturn(props);
        when(databaseLocation.databaseLocation()).thenReturn("midas.db");

        snapshot.create();

        ArgumentCaptor<Properties> captor = ArgumentCaptor.forClass(Properties.class);
        verify(archiveWriter).add(captor.capture());

        Properties capturedProps = captor.getValue();
        assertEquals("jdbc:sqlite:midas.db", capturedProps.getProperty("spring.datasource.url"));
    }

    @Test
    void createWithDefaultUrl() throws IOException {
        Properties props = new Properties();
        props.setProperty("spring.datasource.url", "jdbc:sqlite:midas.db");

        when(propertiesLoader.load()).thenReturn(props);
        when(databaseLocation.databaseLocation()).thenReturn("midas.db");

        snapshot.create();

        ArgumentCaptor<Properties> captor = ArgumentCaptor.forClass(Properties.class);
        verify(archiveWriter).add(captor.capture());

        Properties capturedProps = captor.getValue();
        assertEquals("jdbc:sqlite:midas.db", capturedProps.getProperty("spring.datasource.url"));
    }
}
