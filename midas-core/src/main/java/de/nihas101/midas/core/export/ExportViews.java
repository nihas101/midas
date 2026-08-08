package de.nihas101.midas.core.export;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ExportViews {

    private final Set<LocalizedExportView> localizedExportViews;
    private final Set<ExportViewName> views;

    public ExportViews(final Collection<ExportViewName> views) {
        this(views, null, null);
    }

    public ExportViews(
            final Collection<ExportViewName> views,
            final MessageSource messageSource,
            final Locale locale
    ) {
        this.localizedExportViews = views.stream()
                .map(view -> new LocalizedExportView(
                        view,
                        getLocalizedName(messageSource, locale, view.getName())
                )).collect(Collectors.toCollection(LinkedHashSet::new));
        this.views = localizedExportViews.stream()
                .map(LocalizedExportView::internalName)
                .collect(Collectors.toSet());
    }

    private static String getLocalizedName(
            final MessageSource messageSource,
            final Locale locale,
            final String view
    ) {
        if (messageSource == null) {
            return view;
        }
        if (locale == null) {
            return view;
        }
        return messageSource.getMessage("export.view." + view, null, locale);
    }

    public boolean contains(final ExportViewName view) {
        return views.contains(view);
    }

    public LocalizedExportView[] iterator() {
        return localizedExportViews.toArray(new LocalizedExportView[0]);
    }

    public LocalizedExportView first() {
        return localizedExportViews.iterator().next();
    }

    public int size() {
        return localizedExportViews.size();
    }
}
