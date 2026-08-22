package de.nihas101.midas.core.export.sort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;

@Slf4j
@RequiredArgsConstructor
public class DisplayIdExportRowSort implements ExportRowSort {

    private final Comparator<SortableExportRow> exportRowComparator = Comparator.comparing(
                    SortableExportRow::shareholderId,
                    Comparator.nullsFirst(Comparator.naturalOrder())
            )
            .thenComparing(
                    SortableExportRow::date,
                    Comparator.nullsFirst(Comparator.naturalOrder())
            )
            .thenComparing(
                    SortableExportRow::id,
                    Comparator.nullsFirst(Comparator.naturalOrder())
            );

    @Override
    public int compare(
            final SortableExportRow a,
            final SortableExportRow b
    ) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        if (a.shareholderId() == null && b.shareholderId() == null) {
            return 0;
        }
        if (a.shareholderId() == null) {
            return 1;
        }
        if (b.shareholderId() == null) {
            return -1;
        }
        if (a.date() == null && b.date() == null) {
            return 0;
        }
        if (a.date() == null) {
            return 1;
        }
        if (b.date() == null) {
            return -1;
        }

        return exportRowComparator.compare(a, b);
    }
}