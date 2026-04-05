package de.nihas101.midas.backup.service.snapshot;

import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class SqliteDatabaseLocation implements DatabaseLocation {

    private final String datasourceUrl;
    private final String defaultDatabaseFallback;
    private final String prefix;

    public SqliteDatabaseLocation(final String datasourceUrl) {
        this(
                datasourceUrl,
                "midas.db",
                "jdbc:sqlite:"
        );
    }

    public SqliteDatabaseLocation(final String datasourceUrl, final String defaultDatabaseFallback) {
        this(
                datasourceUrl,
                defaultDatabaseFallback,
                "jdbc:sqlite:"
        );
    }

    @Override
    public String databaseLocation() {
        // e.g. jdbc:sqlite:midas.db -> midas.db
        if (datasourceUrl != null && datasourceUrl.startsWith(prefix)) {
            String path = datasourceUrl.substring(prefix.length());
            return new File(path).getName();
        }
        return defaultDatabaseFallback;
    }
}