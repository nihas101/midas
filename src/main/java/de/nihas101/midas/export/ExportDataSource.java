package de.nihas101.midas.export;

import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

/**
 * Interface for providing data to an exporter in a format-agnostic way.
 */
public interface ExportDataSource {

    /**
     * Returns the name for the sheet or section (e.g., "Bookings").
     */
    String getSheetName(MessageSource messageSource, Locale locale);

    /**
     * Returns the column headers.
     */
    List<String> getHeaders(MessageSource messageSource, Locale locale);

    /**
     * Returns the actual data rows.
     */
    List<List<Object>> getRows();
}
