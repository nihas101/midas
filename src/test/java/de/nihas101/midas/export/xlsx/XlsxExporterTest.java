package de.nihas101.midas.export.xlsx;

import de.nihas101.midas.export.ExportDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class XlsxExporterTest {

    @Mock
    private XlsxExportTargetFactory targetFactory;

    @Mock
    private XlsxExportTarget exportTarget;

    @Mock
    private ExportDataSource dataSource1;

    @Mock
    private ExportDataSource dataSource2;

    @Mock
    private OutputStream outputStream;

    private XlsxExporter underTest;

    @Test
    void trigger_withMultipleDataSources_exportsEachAndWritesToStream() throws Exception {
        when(targetFactory.exportTarget()).thenReturn(exportTarget);
        underTest = new XlsxExporter(List.of(dataSource1, dataSource2), outputStream, targetFactory);

        underTest.trigger();

        verify(dataSource1).export(exportTarget);
        verify(dataSource2).export(exportTarget);
        verify(exportTarget).write(outputStream);
        verify(exportTarget).close();
    }

    @Test
    void trigger_withEmptyDataSources_doesNotWriteToStream() throws Exception {
        underTest = new XlsxExporter(List.of(), outputStream, targetFactory);

        underTest.trigger();

        verifyNoInteractions(dataSource1);
        verify(exportTarget, never()).write(outputStream);
        verify(exportTarget, never()).close();
    }

    @Test
    void trigger_whenTargetFactoryThrows_throwsRuntimeException() {
        when(targetFactory.exportTarget()).thenThrow(new RuntimeException("Factory error"));
        underTest = new XlsxExporter(List.of(dataSource1), outputStream, targetFactory);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> underTest.trigger());
        assertEquals("Export failed", exception.getMessage());
    }

    @Test
    void trigger_whenWriteThrows_throwsRuntimeException() throws Exception {
        when(targetFactory.exportTarget()).thenReturn(exportTarget);
        doThrow(new IOException("Write error")).when(exportTarget).write(outputStream);
        underTest = new XlsxExporter(List.of(dataSource1), outputStream, targetFactory);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> underTest.trigger());
        assertEquals("Export failed", exception.getMessage());
        verify(exportTarget).close();
    }

    @Test
    void trigger_whenDataSourceExportThrows_throwsRuntimeException() throws Exception {
        when(targetFactory.exportTarget()).thenReturn(exportTarget);
        doThrow(new RuntimeException("Export error")).when(dataSource1).export(exportTarget);
        underTest = new XlsxExporter(List.of(dataSource1), outputStream, targetFactory);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> underTest.trigger());
        assertEquals("Export failed", exception.getMessage());
        verify(exportTarget).close();
    }
}
