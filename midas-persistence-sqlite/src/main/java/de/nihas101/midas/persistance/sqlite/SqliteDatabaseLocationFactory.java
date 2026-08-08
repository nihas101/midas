package de.nihas101.midas.persistance.sqlite;

import de.nihas101.midas.persistance.backup.DatabaseLocation;
import de.nihas101.midas.persistance.backup.DatabaseLocationFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "spring.datasource.driver-class-name",
        havingValue = "org.sqlite.JDBC"
)
public class SqliteDatabaseLocationFactory implements DatabaseLocationFactory {

    private final String datasourceUrl;

    public SqliteDatabaseLocationFactory(@Value("${spring.datasource.url}") final String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    @Override
    public DatabaseLocation create() {
        return new SqliteDatabaseLocation(datasourceUrl);
    }
}
