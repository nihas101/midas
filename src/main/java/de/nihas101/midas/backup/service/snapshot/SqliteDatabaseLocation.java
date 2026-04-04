package de.nihas101.midas.backup.service.snapshot;

import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class SqliteDatabaseLocation implements DatabaseLocation { // TODO: Extract interface

    private final String datasourceUrl;
    private final String defaultDatabaseFallback;

    public SqliteDatabaseLocation(final String datasourceUrl) {
        this(datasourceUrl, "midas.db");
    }

    @Override
    public String databaseLocation() {
        // jdbc:sqlite:midas.db -> midas.db
        if (datasourceUrl != null && datasourceUrl.startsWith("jdbc:sqlite:")) {
            String path = datasourceUrl.substring("jdbc:sqlite:".length());
            return new File(path).getName();
        }
        return defaultDatabaseFallback;
    }
}