package de.nihas101.midas.core.export.sort;

import java.time.LocalDate;

public interface SortableExportRow {

    Integer shareholderId();

    LocalDate date();

    Integer id();
}
