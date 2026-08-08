package de.nihas101.midas.core.export.sort;

import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@RequiredArgsConstructor
public class DisplayIdExportRowSort implements ExportRowSort {

    private final Comparator<SortableExportRow> exportRowComparator = Comparator.comparing(SortableExportRow::shareholderId)
            .thenComparing(SortableExportRow::date)
            .thenComparing(SortableExportRow::id);

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